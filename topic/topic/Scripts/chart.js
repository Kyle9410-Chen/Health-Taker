var chart

function createChart(setting) {
    try {
        chart.destroy()
    } catch (e) {

    }
    chart = new Chart("chart", {
        type: "line",
        data: {
            labels: setting.labels,
            datasets: (() => {
                var res = []
                for (var item of setting.datasets) {
                    res.push({
                        label: item.label,
                        data: item.data,
                        fill: false,
                        borderColor: item.color,
                        pointBackgroundColor: item.color,
                        borderWidth: 3,
                        pointBorderWidth: 5,
                        tension: 0
                    })
                }
                return res
            })()
        },
        options: {
            legend: {
                labels: {
                    fontColor: "#fff",
                    fontSize: 18
                },
                onClick: () => { }
            },
            scales: {
                xAxes: [{
                    display: true,
                    gridLines: {
                        color: "#e1e1e155"
                    },
                    ticks: {
                        fontSize: 18,
                        fontColor: "rgba(255,255,255,1)"
                    }
                }],
                yAxes: [{
                    gridLines: {
                        color: "#e1e1e155"
                    },
                    ticks: {
                        fontSize: 18,
                        fontColor: "rgba(255,255,255,1)",
                        beginAtZero: true
                    }
                }],
            },
            plugins: {
                customCanvasBackgroundColor: {
                    color: '#ffd592',
                }
            }
        }
    })
}