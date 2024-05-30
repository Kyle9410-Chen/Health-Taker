using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace topic.Models
{
    public class RegisterModel
    {
        public string Username { get; set; }
        public string IdentityNumber { get; set; }
        public string Password { get; set; }
        public string PasswordConfirm { get; set; }
    }
}