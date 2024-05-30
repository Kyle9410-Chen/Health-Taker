function showSnackBar(text) {
    var element = document.getElementById("snackBar")
    var content = document.getElementById("sB_Content")
    content.innerText = text
    element.classList.add("show")
    setTimeout(() => {
        element.classList.remove("show")
    }, 2950)
}

function titleAnimation() {
    var element = document.getElementById("applicationName");
    for (var i = 0; i < 12; i++) {
        setTimeout((i) => {
            let title = "Health Taker"
            element.innerText += title[i];
        }, i*100 + 1000, i)
    }
}