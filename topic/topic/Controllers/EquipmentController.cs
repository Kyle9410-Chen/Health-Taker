using Microsoft.Ajax.Utilities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Runtime.CompilerServices;

namespace topic.Controllers
{
    public class EquipmentController : ApiController
    {
        [HttpGet]
        public IHttpActionResult getType(string id)
        {
            var db = new TopicEntities();
            var guid = Guid.Parse(id);
            return Ok(JArray.FromObject(db.EquipmentContainTypes.Where(x => x.EquipmentID == guid).Select(x => x.EquipmentType.Name)));
        }

        [HttpPost]
        public IHttpActionResult testPost([FromBody] JObject test)
        {
            test["test"] = "b";
            return Ok(test);
        }

        [HttpPost]
        public IHttpActionResult addTemp([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("MacAddress"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Mac Address founded"
                    });
                }

                if (!data.ContainsKey("Temperature"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Temperature founded in post data"
                    });
                }

                var db = new TopicEntities();
                var macAddress = data.Value<string>("MacAddress");

                if (!db.Equipments.Any(x => x.MacAddress == macAddress && x.EquipmentContainTypes.Any(y => y.EquipmentType.Name == "Temperature")))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Equipment data"
                    });
                }

                Temperature temperature = new Temperature()
                {
                    ID = Guid.NewGuid(),
                    EquipmentID = db.Equipments.FirstOrDefault(x => x.MacAddress == macAddress && x.EquipmentContainTypes.Any(y => y.EquipmentType.Name == "Temperature")).ID,
                    DateTime = DateTime.Now,
                    Value = data.Value<float>("Temperature"),
                };

                db.Temperatures.Add(temperature);
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
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult addHumidity([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("MacAddress"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Mac Address founded"
                    });
                }

                if (!data.ContainsKey("Humidity"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Humidity founded in post data"
                    });
                }

                var db = new TopicEntities();
                var macAddress = data.Value<string>("MacAddress");

                if (!db.Equipments.Any(x => x.MacAddress == macAddress && x.EquipmentContainTypes.Any(y => y.EquipmentType.Name == "Humidity")))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Equipment data"
                    });
                }

                Humidity humidity = new Humidity()
                {
                    ID = Guid.NewGuid(),
                    EquipmentID = db.Equipments.FirstOrDefault(x => x.MacAddress == macAddress && x.EquipmentContainTypes.Any(y => y.EquipmentType.Name == "Temperature")).ID,
                    DateTime = DateTime.Now,
                    Value = data.Value<float>("Humidity"),
                };

                db.Humidities.Add(humidity);
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
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult addFall([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("MacAddress"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Mac Address founded"
                    });
                }

                var db = new TopicEntities();
                var macAddress = data.Value<string>("MacAddress");
                Fall fall = new Fall()
                {
                    ID = Guid.NewGuid(),
                    DateTime = DateTime.Now,
                    EquipmentID = db.Equipments.FirstOrDefault(x => x.MacAddress == macAddress).ID,
                    isChecked = false
                };

                db.Falls.Add(fall);
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
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult addBloodOxygen([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("MacAddress"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Mac Address founded"
                    });
                }

                if (!data.ContainsKey("BloodOxygen"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No BloodOxygen founded in post data"
                    });
                }


                if (data.Value<float>("BloodOxygen") < 90f)
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Error Value"
                    });
                }

                var db = new TopicEntities();
                var macAddress = data.Value<string>("MacAddress");
                BloodOxygen bloodOxygen = new BloodOxygen()
                {
                    ID = Guid.NewGuid(),
                    DateTime = DateTime.Now,
                    EquipmentID = db.Equipments.FirstOrDefault(x => x.MacAddress == macAddress).ID,
                    Value = data.Value<float>("BloodOxygen")
                };

                db.BloodOxygens.Add(bloodOxygen);
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
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult addHeartRate([FromBody] JObject data)
        {
            try
            {
                if (!data.ContainsKey("MacAddress"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No Mac Address founded"
                    });
                }

                if (!data.ContainsKey("HeartRate"))
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "No HeartRate founded in post data"
                    });
                }

                if (data.Value<float>("HeartRate") < 20f)
                {
                    return Ok(new JObject()
                    {
                        ["Status"] = false,
                        ["Content"] = "Error Value"
                    });
                }

                var db = new TopicEntities();
                var macAddress = data.Value<string>("MacAddress");
                HeartRate heartRate = new HeartRate()
                {
                    ID = Guid.NewGuid(),
                    DateTime = DateTime.Now,
                    EquipmentID = db.Equipments.FirstOrDefault(x => x.MacAddress == macAddress).ID,
                    Value = data.Value<float>("HeartRate")
                };

                db.HeartRates.Add(heartRate);
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
                    ["Status"] = false,
                    ["Content"] = "Unexcepted Error"
                });
            }
        }

        [HttpPost]
        public IHttpActionResult getNewHumidity([FromBody] JObject data)
        {
            var db = new TopicEntities();
            var macAddress = data.Value<string>("MacAddress");
            return Ok(JObject.FromObject(new
            {
                humidity = db.Humidities.Where(x => x.Equipment.MacAddress == macAddress).ToList().OrderBy(x => x.DateTime).LastOrDefault()?.Value ?? -1f
            }));
        }
    }
}
