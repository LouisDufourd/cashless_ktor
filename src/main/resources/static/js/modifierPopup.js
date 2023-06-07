let popup = document.getElementById('popup')
let close = document.getElementById('close')
let open = document.getElementsByClassName('open')

popup.style.display = 'none'


for (let i = 0; i < open.length; i++) {
    open[i].addEventListener("click",function() {
        popup.style.display = 'block'
        let values = document.getElementsByClassName(i.toString())
        document.getElementById("idStand").value = values[0].textContent
        document.getElementById("idArticle").value = values[1].textContent
        document.getElementById("price").value = values[2].innerText.replace('€','')
        document.getElementById("quantity").value = values[3].innerText
    })
}
close.addEventListener("click",function (e) {
    popup.style.display  = 'none'
})