using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Security.Cryptography.X509Certificates;
using System.Web.Http;

namespace topic.Controllers
{
    public class ViewController : ApiController
    {
        [HttpPost]
        public IHttpActionResult getTemp([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));
            var type = data.Value<string>("type");

            if (type == "day")
            {
                return Ok(JArray.FromObject(
                    db.Temperatures.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Date == date.Date)
                    .GroupBy(x => x.DateTime.Hour)
                    .Select(x => new
                    {
                        Hour = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            else if (type == "month")
            {
                return Ok(JArray.FromObject(
                    db.Temperatures.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy(x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            return Ok();
        }

        [HttpPost]
        public IHttpActionResult getHumidity([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));
            var type = data.Value<string>("type");

            if (type == "day")
            {
                return Ok(JArray.FromObject(
                    db.Humidities.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Date == date.Date)
                    .GroupBy(x => x.DateTime.Hour)
                    .Select(x => new
                    {
                        Hour = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            else if (type == "month")
            {
                return Ok(JArray.FromObject(
                    db.Humidities.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy (x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            return Ok();
        }


        [HttpPost]
        public IHttpActionResult getBloodPressure([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));

            return Ok(JArray.FromObject(
                    db.BloodPressures.Where(x => x.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy(x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Systolic = Math.Round(x.Max(y => y.SystolicValue), 1),
                        Diastolic = Math.Round(x.Max(y => y.DiastolicValue), 1)
                    })
                ));
        }

        [HttpPost]
        public IHttpActionResult getFall([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));

            return Ok(JArray.FromObject(
                    db.Falls.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy(x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Value = x.Count()
                    })
                ));
        }

        [HttpGet]
        public IHttpActionResult getFall(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);

            return Ok(new JObject()
            {
                ["Value"] = db.Falls.Where(x => !x.isChecked && x.Equipment.CareRecipientID == guid).Count()
            });
        }

        [HttpPost]
        public IHttpActionResult checkFall([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(data.Value<string>("id"));
            var fall = db.Falls.FirstOrDefault(x => x.ID == guid);
            fall.CheckDateTime = DateTime.Now;
            fall.isChecked = true;
            fall.CaregiverID = Guid.Parse(data.Value<string>("caregiver"));
            db.SaveChanges();
            return Ok();
        }

        [HttpPost]
        public IHttpActionResult getBloodOxygen([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));
            var type = data.Value<string>("type");

            if (type == "day")
            {
                return Ok(JArray.FromObject(
                    db.BloodOxygens.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Date == date.Date)
                    .GroupBy(x => x.DateTime.Hour)
                    .Select(x => new
                    {
                        Hour = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            else if (type == "month")
            {
                return Ok(JArray.FromObject(
                    db.BloodOxygens.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy(x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Value = Math.Round(x.Average(y => y.Value), 1)
                    })
                ));
            }
            return Ok();
        }

        [HttpPost]
        public IHttpActionResult getHeartRate([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var id = Guid.Parse(data.Value<string>("id"));
            var date = DateTime.Parse(data.Value<string>("date"));
            var type = data.Value<string>("type");

            if (type == "day")
            {
                return Ok(JArray.FromObject(
                    db.HeartRates.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Date == date.Date)
                    .GroupBy(x => x.DateTime.Hour)
                    .Select(x => new
                    {
                        Hour = x.Key,
                        Value = Math.Round(x.Max(y => y.Value), 1)
                    })
                ));
            }
            else if (type == "month")
            {
                return Ok(JArray.FromObject(
                    db.HeartRates.Where(x => x.Equipment.CareRecipientID == id)
                    .ToList()
                    .Where(x => x.DateTime.Year == date.Year && x.DateTime.Month == date.Month)
                    .GroupBy(x => x.DateTime.Day)
                    .Select(x => new
                    {
                        Day = x.Key,
                        Value = Math.Round(x.Max(y => y.Value), 1)
                    })
                ));
            }
            return Ok();
        }
    }
}
