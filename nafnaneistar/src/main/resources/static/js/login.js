document.addEventListener('DOMContentLoaded', () => {
    const loginform = document.querySelector('.login__form');
    loginform.addEventListener('submit', submitHandler);
    var interval;

    function submitHandler(e) {
        e.preventDefault();
        let error = document.querySelector('.error__message');
        error.classList.add('--hidden')
        let user = document.querySelector('#email').value
        let password = document.querySelector('#password').value
        let url = `${window.location.origin}/login/check?email=${user}&password=${password}`
        validateLogin(url)
    }

    function decreaseTimer() {
        let timer = document.querySelector('.popup__timer');
        let error = document.querySelector('.error__message');
        let ptitle = document.querySelector('.popup__title');
        let code = (error.classList.contains('--hidden')) ? 1 : 0;
        let value = parseInt(timer.textContent);
        timer.textContent = parseInt(value - 1);
        let url = `${window.location.origin}/swipe`
        if (value <= 1) {
            code = (error.classList.contains('--hidden')) ? 1 : 0;
            if (code == 1) {
                ptitle.textContent = "Innskráning Tókst!"
                window.location.replace(url)
            }
            else
                document.querySelector('.popup').remove()
            clearInterval(interval)
        }
    }

    function el(tag, className, text = null) {
        let element = document.createElement(tag)
        element.classList.add(className)
        if (text)
            element.appendChild(document.createTextNode(text))
        return element;
    }

    function createPopup(title, text, timer) {
        let popupContainer = el('div', 'popup__container')
        let popupTitle = el('h2', 'popup__title', title)
        popupContainer.appendChild(popupTitle)

        if (text) {
            let popupText = el('p', 'popup__text', text)
            popupContainer.appendChild(popupText)
        }
        let popupTimer = el('span', 'popup__timer', timer)
        popupTimer.classList.add('hidden');
        popupContainer.appendChild(popupTimer)

        let loading = el('img', 'popup__img')
        loading.setAttribute('src', "/img/loading.gif")
        popupContainer.appendChild(loading)

        let popup = el('div', 'popup')
        popup.appendChild(popupContainer);
        document.querySelector('body').appendChild(popup)
    }

    function validateLogin(url) {
        createPopup("Skrái inn...", null, 1);
        fetch(url).then((resp) => {
            if (resp.status !== 200) {
                console.error(`Error ${resp.text()}`);
                return
            }
            return resp.json()
        }).then((data) => {
            if (!data)
                ShowError()
            interval = setInterval(decreaseTimer, 1000)
        })
    }
    function ShowError(message = null) {
        let error = document.querySelector('.error__message');
        let password = document.querySelector('#password');
        let username = document.querySelector('#email');
        if (username.value.trim().length === 0)
            message = "Vinsamlegast sláðu inn netfang"
        if (!username.value.includes("@"))
            message = "Ath að slá inn netfangið þitt"
        else if (password.value.trim().length === 0)
            message = "Vinsamlegast sláðu inn lykilorð"
        error.classList.remove("--hidden")
        if (message)
            error.textContent = message
    }
});
