using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace topic.Service
{
    public class UserToken
    {
        public string PasswordHash { get; set; }
        public string PasswordSalt { get; set; }
        public int PasswordWorkFactor { get; set; }
    }
}