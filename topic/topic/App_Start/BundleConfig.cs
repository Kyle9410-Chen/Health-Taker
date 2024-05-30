using System.Web;
using System.Web.Optimization;

namespace topic
{
    public class BundleConfig
    {
        // For more information on bundling, visit https://go.microsoft.com/fwlink/?LinkId=301862
        public static void RegisterBundles(BundleCollection bundles)
        {
            // Use the development version of Modernizr to develop with and learn from. Then, when you're
            // ready for production, use the build tool at https://modernizr.com to pick only the tests you need.


            //custom
            bundles.Add(new ScriptBundle("~/bundles/main").Include(
            "~/Scripts/main.js",
            "~/Scripts/alert.js",
            "~/Scripts/model.js",
            "~/Scripts/chart.js",
            "~/Scripts/select.js"
            ));

            bundles.Add(new StyleBundle("~/Content/css").Include(
          "~/Content/main.css",
          "~/Content/alert.css",
          "~/Content/snackbar.css",
          "~/Content/dialog.css",
          "~/Content/tableData.css",
          "~/Content/select.css"
          ));
        }
    }
}
