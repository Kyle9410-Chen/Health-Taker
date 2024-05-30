using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace topic.Models
{
    public class EquipmentModel
    {
        public string DisplayName { get; set; }
        public string MacAddress { get; set; }
        public List<string> Type { get; set; }
        public Guid ID { get; set; }
        public bool Action { get; set; }
    }
}