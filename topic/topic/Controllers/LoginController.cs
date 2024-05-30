using Microsoft.Ajax.Utilities;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using System.Web.UI;
using topic.Models;
using topic.Service;

namespace topic.Controllers
{
    public class LoginController : Controller
    {
        // GET: Login
        [HttpGet]
        public ActionResult Login()
        {
            ViewData["Status"] = 0;
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Login(LoginModel data)
        {
            if (!ModelState.IsValid)
            {
                return View();
            }

            if (new string[] { data.Username, data.Password }.Any(x => x.IsNullOrWhiteSpace()))
            {
                TempData.Add("Status", 3);
                return View();
            }

            var userService = new UserService();
            var user = userService.ValidateCaregiver(data);

            if (user != null)
            {
                var cookie = new UserService().GenerateCookie(user);
                Response.Cookies.Add(cookie);

                TempData.Add("Status", 1);
                return RedirectToAction("Index", "Home");
            }

            TempData.Add("Status", 2);
            return View();
        }

        [HttpGet]
        public ActionResult Register()
        {
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Register(RegisterModel data)
        {
            if (!ModelState.IsValid)
            {
                return View();
            }

            UserService service = new UserService();
            var createStatus = service.CreateCaregiver(data);
            if (createStatus == 1)
            {
                TempData["Status"] = 1;
                return RedirectToAction("Login");
            }

            TempData["Status"] = createStatus;
            return View();
        }

        [HttpGet]
        public ActionResult Logout()
        {
            FormsAuthentication.SignOut();
            Session.RemoveAll();
            return RedirectToAction("Index", "Home");
        }
    }
}