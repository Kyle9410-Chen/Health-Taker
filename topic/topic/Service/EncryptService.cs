using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace topic.Service
{
    public class EncryptService
    {
        public int SaltLength { get; private set; }
        public int HashLength { get; private set; }
        public int Iteration { get; private set; }

        public EncryptService() : this(256, 256, 5)
        {

        }

        public EncryptService(int saltLength, int hashLength, int iteration)
        {
            SaltLength = saltLength;
            HashLength = hashLength;
            Iteration = iteration;
        }

        public UserToken GenerateUserToken(string password)
        {
            var encode = Base64Encode(password);
            var salt = GenerateSalt(SaltLength);
            var hash = GenerateHash(Convert.FromBase64String(encode), salt, Iteration, HashLength);

            UserToken token = new UserToken()
            {
                PasswordHash = Convert.ToBase64String(hash),
                PasswordSalt = Convert.ToBase64String(salt),
                PasswordWorkFactor = Iteration,
            };

            return token;
        }
        public bool IsValidPassword(string inputHash, string inputSalt, string password)
        {
            var encodePass = Base64Encode(password);
            var hash = GenerateHash(Convert.FromBase64String(encodePass), Convert.FromBase64String(inputSalt), Iteration, HashLength);
            var hasString = Convert.ToBase64String(hash);
            return inputHash.Equals(hasString);
        }


        private byte[] GenerateSalt(int length)
        {
            var bytes = new byte[length];
            using (var rng = new RNGCryptoServiceProvider())
            {
                rng.GetBytes(bytes);
            }
            return bytes;
        }

        private byte[] GenerateHash(byte[] password, byte[] salt, int iterations, int length)
        {
            using (var deriveBytes = new Rfc2898DeriveBytes(password, salt, iterations))
            {
                return deriveBytes.GetBytes(length);
            }
        }

        private static string Base64Encode(string plainText)
        {
            var plainTextBytes = Encoding.UTF8.GetBytes(plainText);
            return Convert.ToBase64String(plainTextBytes);
        }
    }
}