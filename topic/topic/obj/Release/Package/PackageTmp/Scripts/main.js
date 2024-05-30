function showSnackBar(text) {
    var element = document.getElementById("snackBar")
    var content = document.getElementById("sB_Content")
    content.innerText = text
    element.classList.add("show")
    setTimeout(() => {
        element.classList.remove("show")
    }, 2950);
}

var chart

function createChart() {
    console.log(1)
    try {
        chart.destroy()
    } catch (e) {

    }

    chart = new Chart("chart", {
        type: "line",
        data: {
            labels: ["asd", "asd", "asd"],
            datasets: [{
                label: "test",
                fill: false,
                lineTension: 0,
                pointRadius: 5,
                borderWidth: 5,
                backgroundColor: "#f00",
                borderColor: "#f00",
                data: [1, 2, 3]
            }]
        },
        options: {
            legend: {
                labels: {
                    fontColor: "#000",
                    fontSize: 18
                }
            },
            scales: {
                xAxes: [{
                    display: true,
                    gridLines: {
                        color: "#e1e1e155"
                    },
                    ticks: {
                        fontSize: 18,
                        fontColor: "rgba(0,0,0,1)"
                    }
                }],
                yAxes: [{
                    gridLines: {
                        color: "#e1e1e155"
                    },
                    ticks: {
                        fontSize: 18,
                        fontColor: "rgba(0,0,0,1)",
                        max: 100,
                        min: 0
                    }
                }],
            }
        }
    })
}