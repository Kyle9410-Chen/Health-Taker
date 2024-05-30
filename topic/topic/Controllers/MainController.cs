using Microsoft.Ajax.Utilities;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Data.Entity.Migrations;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.Http.Filters;
using System.Web.Mvc;
using System.Web.Security;
using topic.Models;

namespace topic.Controllers
{
    [Authorize]
    public class MainController : Controller
    {
        // GET: Main
        [HttpGet]
        public ActionResult Main()
        {
            var db = new TopicEntities();
            var id =  FormsAuthentication.Decrypt(Request.Cookies[FormsAuthentication.FormsCookieName].Value).UserData;
            var user = db.Caregivers.FirstOrDefault(x => x.ID.ToString() == id);
            ViewBag.Data = user.CarePairs.Select(x => x.CareRecipient).ToList();
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Main(CareRecipientModel data)
        {
            if (!ModelState.IsValid)
            {
                return RedirectToAction("Main", "Main");
            }

            var db = new TopicEntities();
            var id = Guid.Parse(FormsAuthentication.Decrypt(Request.Cookies[FormsAuthentication.FormsCookieName].Value).UserData);
            if (db.CareRecipients.FirstOrDefault(x => x.Username == data.Name && x.IdentityNumber == data.IdentityNumber) is { } cr)
            {
                if (db.CarePairs.Any(x => x.CareRecipientID == cr.ID && x.CaregiverID == id))
                {
                    TempData["Status"] = 3;
                    return RedirectToAction("Main", "Main");
                }
                db.CarePairs.Add(new CarePair()
                {
                    ID = Guid.NewGuid(),
                    CaregiverID = id,
                    CareRecipientID = cr.ID,
                });
            }
            else
            {
                TempData["Status"] = 2;
                return RedirectToAction("Main", "Main");
            }
            
            db.SaveChanges();

            TempData["Status"] = 1;
            return RedirectToAction("Main", "Main");
        }

        [HttpGet]
        public ActionResult CareRecipientDetail(string id)
        {
            var db = new TopicEntities();
            ViewBag.Data = db.EquipmentTypes.ToList();
            var caregiver = FormsAuthentication.Decrypt(Request.Cookies[FormsAuthentication.FormsCookieName].Value).UserData;
            var guid = Guid.Parse(id);
            ViewBag.Equipments = db.Equipments.Where(x => x.CareRecipientID == guid).ToList().Select(x => new
            {
                ID = x.ID,
                DisplayName = x.DisplayName,
                Type = JArray.FromObject(x.EquipmentContainTypes.Select(x => x.EquipmentType.Name)).ToString(),
                MacAddress = x.MacAddress,
                LastUpdate = x.Temperatures.Count() == 0 ? x.CreateDate : x.Temperatures.DefaultIfEmpty().Select(x => x.DateTime).OrderBy(x => x).LastOrDefault()
            }).ToList();
            var data = db.Equipments.Where(x => x.CareRecipientID == guid).SelectMany(x => x.EquipmentContainTypes.Select(y => y.EquipmentType.Name)).Distinct().ToList();
            data.Add("Blood Pressure");
            ViewBag.Type = data;
            ViewBag.Fall = db.Falls.Where(x => x.Equipment.CareRecipientID == guid && !x.isChecked).ToList().Select(x => new
            {
                ID = x.ID.ToString(),
                DateTime = x.DateTime.ToString("yyyy/MM/dd hh:mm:ss"),
            });
            ViewBag.FallCount = db.Falls.Count(x => x.Equipment.CareRecipientID == guid && !x.isChecked);

            ViewData["ID"] = id;
            ViewData["Caregiver"] = caregiver;
            return View();
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult AddEquipment(EquipmentModel data)
        {
            if (!ModelState.IsValid)
            {
                return RedirectToAction("CareRecipientDetail", new {id = data.ID});
            }
            var regex = new Regex("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$");
            var lengthCheck = new Regex("^.{1,20}$");
            var typeCheck = new Regex("^[a-zA-Z0-9]+$");
            data.Type = data.Type ?? new List<string>();

            if (new string[] { data.DisplayName, data.MacAddress }.Any(x => x == null || x == "") || (data.Type.Count() == 0 && data.Action))
            {
                TempData["Status"] = 2;
                return RedirectToAction("CareRecipientDetail", new { id = data.ID });
            }

            if (!regex.IsMatch(data.MacAddress))
            {
                TempData["Status"] = 3;
                return RedirectToAction("CareRecipientDetail", new { id = data.ID });
            }

            var db = new TopicEntities();
            
            if (db.Equipments.Any(x => x.MacAddress == data.MacAddress) && data.Action)
            {
                TempData["Status"] = 4;
                return RedirectToAction("CareRecipientDetail", new { id = data.ID });
            }


            if (!lengthCheck.IsMatch(data.DisplayName))
            {
                TempData["Status"] = 5;
                return RedirectToAction("CareRecipientDetail", new { id = data.ID });
            }

            if (!typeCheck.IsMatch(data.DisplayName))
            {
                TempData["Status"] = 6;
                return RedirectToAction("CareRecipientDetail", new { id = data.ID });
            }

            var equipment = db.Equipments.FirstOrDefault(x => x.MacAddress == data.MacAddress) ?? new Equipment()
            {
                ID = Guid.NewGuid(),
                CareRecipientID = data.ID,
                DisplayName = data.DisplayName,
                CreateDate = DateTime.Now,
                MacAddress = data.MacAddress,
            };

            db.Equipments.AddOrUpdate(equipment);
            db.SaveChanges();

            foreach (string type in data.Type)
            {
                var equipType = db.EquipmentContainTypes.FirstOrDefault(x => x.EquipmentID == equipment.ID && x.EquipmentType.Name == type) ?? new EquipmentContainType()
                {
                    ID = Guid.NewGuid(),
                    EquipmentID = equipment.ID,
                    EquipmentTypeID = db.EquipmentTypes.FirstOrDefault(x => x.Name == type).ID
                };
                db.EquipmentContainTypes.AddOrUpdate(equipType);
            }
            db.SaveChanges();

            foreach (var pairType in db.EquipmentContainTypes.Where(x => x.Equipment.ID == equipment.ID))
            {
                if (!data.Type.Contains(pairType.EquipmentType.Name))
                {
                    db.EquipmentContainTypes.Remove(db.EquipmentContainTypes.FirstOrDefault(x => x.EquipmentID == pairType.EquipmentID && x.EquipmentTypeID == pairType.EquipmentTypeID));
                }
            }
            db.SaveChanges();

            if (equipment.EquipmentContainTypes.Count() == 0)
            {
                db.Equipments.Remove(equipment);
                db.SaveChanges();
            }

            return RedirectToAction("CareRecipientDetail", new { id = data.ID });
        }
    }
}