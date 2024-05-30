using Microsoft.Ajax.Utilities;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Security;
using topic.Models;

namespace topic.Service
{
    public class UserService
    {
        TopicEntities db = new TopicEntities();

        private Dictionary<char, int> IdentityMapping = new Dictionary<char, int>()
        {
            ['A'] = 10, 
            ['B'] = 11, 
            ['C'] = 12, 
            ['D'] = 13, 
            ['E'] = 14, 
            ['F'] = 15, 
            ['G'] = 16, 
            ['H'] = 17, 
            ['J'] = 18, 
            ['K'] = 19, 
            ['L'] = 20, 
            ['M'] = 21, 
            ['N'] = 22,
            ['P'] = 23,
            ['Q'] = 24,
            ['R'] = 25,
            ['S'] = 26,
            ['T'] = 27,
            ['U'] = 28,
            ['V'] = 29,
            ['X'] = 30,
            ['Y'] = 31,
            ['W'] = 32,
            ['Z'] = 33,
            ['I'] = 34,
            ['O'] = 35
        };
        private bool HasCaregiver(string username)
        {
            return new TopicEntities().Caregivers.Any(x => x.Username == username);
        }

        private bool HasCareRecipient(string username)
        {
            return new TopicEntities().CareRecipients.Any(x => x.Username == username);
        }

        private bool HasPersonInCaregiver(string identityNumber)
        {
            return new TopicEntities().Caregivers.Any(x => x.IdentityNumber == identityNumber);
        }

        private bool HasPersonInCareRecipient(string identityNumber)
        {
            return new TopicEntities().CareRecipients.Any(x => x.IdentityNumber == identityNumber);
        }

        private Caregiver GetCaregiver(string username)
        {
            return new TopicEntities().Caregivers.FirstOrDefault(x => x.Username == username);
        }

        private CareRecipient GetCareRecipient(string username)
        {
            return new TopicEntities().CareRecipients.FirstOrDefault(x => x.Username == username);
        }

        private bool IdentityCheck(string identityNumber)
        {
            if (identityNumber.Length != 10) return false;
            var sum = 0;
            for (int i = 0; i < 9; i++)
            {
                if (i == 0)
                {
                    var value = IdentityMapping[identityNumber[0]];
                    sum += value / 10 + value % 10 * 9;
                }
                else
                {
                    sum += int.Parse(identityNumber[i].ToString()) * (9 - i);
                }
            }

            return sum % 10 == int.Parse(identityNumber.Last().ToString()) || 10 - sum % 10 == int.Parse(identityNumber.Last().ToString());
        }

        public HttpCookie GenerateCookie(Caregiver user)
        {
            var ticket = new FormsAuthenticationTicket(
                    version: 1,
                    name: user.Username,
                    issueDate: DateTime.Now,
                    expiration: DateTime.Now.AddMinutes(120),
                    isPersistent: true,
                    userData: user.ID.ToString(),
                    cookiePath: FormsAuthentication.FormsCookiePath
                );

            var encryptedTicket = FormsAuthentication.Encrypt(ticket);
            var cookie = new HttpCookie(FormsAuthentication.FormsCookieName, encryptedTicket)
            {
                HttpOnly = true,
            };

            return cookie;
        }

        public int CreateCaregiver(RegisterModel data)
        {
            var lengthCheck = new Regex("^.{1,20}$");
            var typeCheck = new Regex("^[a-zA-Z0-9]+$");
            
            if (HasCaregiver(data.Username)) return 2;
            if (!IdentityCheck(data.IdentityNumber)) return 3;
            if (HasPersonInCaregiver(data.IdentityNumber)) return 4;
            if (data.Password != data.PasswordConfirm) return 5;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => x == "" || x == null)) return 6;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => !lengthCheck.IsMatch(x))) return 7;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => !typeCheck.IsMatch(x))) return 8;


            UserToken token = new EncryptService().GenerateUserToken(data.Password);
            var user = new Caregiver()
            {
                ID = Guid.NewGuid(),
                Username = data.Password,
                IdentityNumber = data.IdentityNumber,
                PasswordHash = token.PasswordHash,
                PasswordSalt = token.PasswordSalt,
                PasswordWorkFactor = token.PasswordWorkFactor
            };

            db.Caregivers.Add(user);
            db.SaveChanges();
            return 1;
        }

        public int CreateCareRecipient(RegisterModel data)
        {
            var lengthCheck = new Regex("^.{1,20}$");
            var typeCheck = new Regex("^[a-zA-Z0-9]+$");
            
            if (HasCareRecipient(data.Username)) return 2;
            if (!IdentityCheck(data.IdentityNumber)) return 3;
            if (HasPersonInCareRecipient(data.Username)) return 4;
            if (data.Password != data.PasswordConfirm) return 5;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => x == "" || x == null)) return 6;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => !lengthCheck.IsMatch(x))) return 7;
            if (new string[] { data.Username, data.Password, data.PasswordConfirm }.Any(x => !typeCheck.IsMatch(x))) return 8;


            UserToken token = new EncryptService().GenerateUserToken(data.Password);
            var user = new CareRecipient()
            {
                ID = Guid.NewGuid(),
                Username = data.Password,
                IdentityNumber = data.IdentityNumber,
                PasswordHash = token.PasswordHash,
                PasswordSalt = token.PasswordSalt,
                PasswordWorkFactor = token.PasswordWorkFactor
            };

            db.CareRecipients.Add(user);
            db.SaveChanges();
            return 1;
        }

        public Caregiver ValidateCaregiver(LoginModel data)
        {
            if (HasCaregiver(data.Username))
            {
                Caregiver user = GetCaregiver(data.Username);
                bool isValid = new EncryptService().IsValidPassword(user.PasswordHash, user.PasswordSalt, data.Password);
                return isValid ? user : null;
            }
            return null;
        }

        public CareRecipient ValidateCareRecipient(LoginModel data)
        {
            if (HasCareRecipient(data.Username))
            {
                CareRecipient user = GetCareRecipient(data.Username);
                bool isValid = new EncryptService().IsValidPassword(user.PasswordHash, user.PasswordSalt, data.Password);
                return isValid ? user : null;
            }
            return null;
        }

    }
}