using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using topic.Service;

namespace topic.Controllers
{
    public class MobileController : ApiController
    {
        [HttpPost]
        public IHttpActionResult login([FromBody] JObject data)
        {
            try
            {
                var db = new TopicEntities();
                if (!data.ContainsKey("username"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Username not found"
                    });
                }
                if (!data.ContainsKey("password"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Password not found"
                    });
                }

                var username = data.Value<string>("username");
                var password = data.Value<string>("password");
                var isCaregiver = data.Value<bool>("isCaregiver");
                var userService = new UserService();
                if (isCaregiver)
                {
                    var caregiver = userService.ValidateCaregiver(new Models.LoginModel()
                    {
                        Username = username,
                        Password = password
                    });

                    if (caregiver == null)
                    {
                        return Ok(new JObject()
                        {
                            ["Status"] = false,
                            ["Content"] = "Username or Password was wrong"
                        });
                    }
                    return Ok(new JObject()
                    {
                        ["Status"] = true,
                        ["ID"] = caregiver.ID,
                        ["isCaregiver"] = true,
                    });
                }
                else
                {
                    var careRecipient = userService.ValidateCareRecipient(new Models.LoginModel()
                    {
                        Username = username,
                        Password = password
                    });

                    if (careRecipient == null)
                    {
                        return Ok(new JObject()
                        {
                            ["Status"] = false,
                            ["Content"] = "Username or Password was wrong"
                        });
                    }
                    return Ok(new JObject()
                    {
                        ["Status"] = true,
                        ["ID"] = careRecipient.ID,
                        ["isCaregiver"] = false,
                        ["Username"] = careRecipient.Username
                    });
                }
            }
            catch (Exception ex)
            {
                return Ok(new JObject()
                {
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error",
                    ["ErrorText"] = ex.ToString()
                });
            }
        }

        [HttpPost]
        public IHttpActionResult create([FromBody] JObject data)
        {
            try
            {
                var db = new TopicEntities();
                if (!data.ContainsKey("username"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Username not found"
                    });
                }
                if (!data.ContainsKey("password"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Password not found"
                    });
                }

                var username = data.Value<string>("username");
                var password = data.Value<string>("password");
                var passwordConfirm = data.Value<string>("passwordConfirm");
                var identityNumber = data.Value<string>("identityNumber");
                var isCaregiver = data.Value<bool>("isCaregiver");
                var userService = new UserService();
                if (isCaregiver)
                {
                    var caregiver = userService.CreateCaregiver(new Models.RegisterModel()
                    {
                        Username = username,
                        Password = password,
                        PasswordConfirm = passwordConfirm,
                        IdentityNumber = identityNumber,

                    });

                    if (caregiver == 1)
                    {
                        return Ok(new JObject()
                        {
                            ["Status"] = true,
                        });
                    }

                    else
                    {
                        var jsonData = new JObject()
                        {
                            ["Status"] = false,
                        };
                        switch (caregiver)
                        {
                            case 2:
                                jsonData.Add("Content", "Username is already exist");
                                break;
                            case 3:
                                jsonData.Add("Content", "Incorrect format for Identity Number");
                                break;
                            case 4:
                                jsonData.Add("Content", "Identity Number is already exist");
                                break;
                            case 5:
                                jsonData.Add("Content", "Password isn't equals to Confirm Password");
                                break;
                            case 6:
                                jsonData.Add("Content", "Exist blank fields");
                                break;
                            case 7:
                                jsonData.Add("Content", "The field can only contain numbers and letters");
                                break;
                            case 8:
                                jsonData.Add("Content", "The field length cannot be more than 20 characters");
                                break;
                        }
                        return Ok(jsonData);
                    }

                }
                else
                {

                    var careRecipient = userService.CreateCareRecipient(new Models.RegisterModel()
                    {
                        Username = username,
                        Password = password,
                        PasswordConfirm = passwordConfirm,
                        IdentityNumber = identityNumber,

                    });

                    if (careRecipient == 1)
                    {
                        return Ok(new JObject()
                        {
                            ["Status"] = true,
                        });
                    }

                    else
                    {
                        var jsonData = new JObject()
                        {
                            ["Status"] = false,
                        };
                        switch (careRecipient)
                        {
                            case 2:
                                jsonData.Add("Content", "Username is already exist");
                                break;
                            case 3:
                                jsonData.Add("Content", "Incorrect format for Identity Number");
                                break;
                            case 4:
                                jsonData.Add("Content", "Identity Number is already exist");
                                break;
                            case 5:
                                jsonData.Add("Content", "Password isn't equals to Confirm Password");
                                break;
                            case 6:
                                jsonData.Add("Content", "Exist blank fields");
                                break;
                            case 7:
                                jsonData.Add("Content", "The field can only contain numbers and letters");
                                break;
                            case 8:
                                jsonData.Add("Content", "The field length cannot be more than 20 characters");
                                break;
                        }
                        return Ok(jsonData);
                    }
                }
            }
            catch
            {
                return Ok(new JObject()
                {
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult addBloodPressure([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("id") || !data.ContainsKey("systolic") || !data.ContainsKey("diastolic"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Incomplete Data",
                    });
                }

                var guid = Guid.Parse(data.Value<string>("id"));
                var systolic = data.Value<float>("systolic");
                var diastolic = data.Value<float>("diastolic");

                var db = new TopicEntities();
                db.BloodPressures.Add(new BloodPressure()
                {
                    ID = Guid.NewGuid(),
                    CareRecipientID = guid,
                    SystolicValue = Math.Round(systolic, 1),
                    DiastolicValue = Math.Round(diastolic, 1),
                    DateTime = DateTime.Now,
                });

                db.SaveChanges();

                return Ok(new JObject()
                {
                    ["Status"] = true
                });
            }
            catch
            {
                return Ok(new JObject()
                {
                    ["Status"] = false,
                    ["Content"] = "Unexcepted error"
                });
            }
        }

        [HttpGet]
        public IHttpActionResult getEquipment(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);

            return Ok(JArray.FromObject(db.Equipments.Where(x => x.CareRecipientID == guid).Select(x => new
            {
                ID = x.ID,
                DisplayName = x.DisplayName,
                Type = x.EquipmentContainTypes.Select(y => y.EquipmentType.Name)
            })));
        }

        [HttpGet]
        public IHttpActionResult getCareRecipient(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.CarePairs.Where(x => x.CaregiverID == guid).Select(x => new
            {
                ID = x.CareRecipientID,
                Username = x.CareRecipient.Username
            })));
        }

        [HttpGet]
        public IHttpActionResult getTemperature(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.Temperatures.Where(x => x.Equipment.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                Temperature = Math.Round(x.Value, 1),
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpGet]
        public IHttpActionResult getHumidity(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.Humidities.Where(x => x.Equipment.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                Humidity = Math.Round(x.Value, 1),
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpGet]
        public IHttpActionResult getBloodPressure(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.BloodPressures.Where(x => x.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                Systolic = Math.Round(x.SystolicValue, 1),
                Diastolic = Math.Round(x.DiastolicValue, 1),
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpGet]
        public IHttpActionResult getFall(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.Falls.Where(x => x.Equipment.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                isChecked = x.isChecked,
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpGet]
        public IHttpActionResult getNewFall(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.Falls.Where(x => db.CarePairs.Where(x => x.CaregiverID == guid).Any(y => y.CareRecipientID == x.Equipment.CareRecipientID)).ToList().Select(x => new
            {
                ID = x.ID,
                isChecked = x.isChecked,
                CareRecipient = x.Equipment.CareRecipient.Username,
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpPost]
        public IHttpActionResult checkFall([FromBody] JObject data)
        {
            try
            {
                var db = new TopicEntities();
                var guid = Guid.Parse(data.Value<string>("id"));
                var fall = db.Falls.FirstOrDefault(x => x.ID == guid);
                fall.CheckDateTime = DateTime.Now;
                fall.isChecked = true;
                fall.CaregiverID = Guid.Parse(data.Value<string>("caregiver"));
                db.SaveChanges();

                return Ok(new JObject()
                {
                    ["Status"] = true
                });
            }
            catch (Exception ex)
            {
                return Ok(new JObject()
                {
                    ["Status"] = false
                });
            }
            
            
        }


        [HttpGet]
        public IHttpActionResult getHeartRate(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.HeartRates.Where(x => x.Equipment.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                HeartRate = Math.Round(x.Value, 1),
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }

        [HttpGet]
        public IHttpActionResult getBloodOxygen(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.BloodOxygens.Where(x => x.Equipment.CareRecipientID == guid).ToList().OrderByDescending(x => x.DateTime).Select(x => new
            {
                ID = x.ID,
                BloodOxygen = Math.Round(x.Value, 1),
                DateTime = x.DateTime.ToString("yyyy/MM/dd HH:mm"),
            })));
        }
    }
}
