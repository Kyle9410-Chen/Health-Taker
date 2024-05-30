function toggleSelect(element) {
    element.parentElement.classList.toggle("open")
}

function checkValue(data, name) {
    var element = document.getElementById(name)
    element.textContent = data;
    element.parentElement.parentElement.classList.remove("open");

    if (name === "equipmentType") {
        document.getElementById("Type").value = data
        return
    }

    if (name === "dataType") {
        var chart = document.getElementById("chart")
        chart.classList.add("show")
        var dateType = document.getElementById("defaultDateType")
        var typeDay = document.getElementById("type_Day")
        dateType.click()
        typeDay.click()


        if (data === "Temperature") {
            showTempData()
            document.getElementById("dateTypeFilter").classList.remove("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "collapse"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        if (data === "Humidity") {
            showHumidityData()
            document.getElementById("dateTypeFilter").classList.remove("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "collapse"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        if (data === "Heart Rate") {
            showHeartRateData()
            document.getElementById("dateTypeFilter").classList.remove("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "collapse"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        if (data === "Blood Oxygen") {
            showBloodOxygenData()
            document.getElementById("dateTypeFilter").classList.remove("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "collapse"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        if (data === "Blood Pressure") {
            showBloodData()
            document.getElementById("dateTypeFilter").classList.add("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "collapse"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        if (data === "Fall") {
            showFallData();
            document.getElementById("dateTypeFilter").classList.add("close")
            document.getElementById("dateTypeFilter").children[1].classList.remove("open")
            document.getElementById("fallTable").style.visibility = "visible"
            document.getElementById("dateFilter").classList.remove("close")
            document.getElementById("DataDate").value = new Date(Date.now()).toISOString().substring(0, 10)
        }
        return
    }

    if (name === "dateType") {
        var dataType = document.getElementById("dataType")
        if (dataType.innerText === "Temperature") {
            showTempData()
        }
        if (dataType.innerText === "Humidity") {
            showHumidityData()
        }
        if (dataType.innerText === "Blood Pressure") {
            showBloodData()
        }
        if (dataType.innerText === "Fall") {
            showFallData()
        }
        if (dataType.innerText === "Blood Oxygen") {
            showBloodOxygenData()
        }
        if (dataType.innerText === "Heart Rate") {
            showHeartRateData()
        }
        return
    }
}