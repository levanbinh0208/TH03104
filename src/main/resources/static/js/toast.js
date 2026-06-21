function closeToast() {
    const toast = document.querySelector(".toast");

    if (toast) {
        toast.classList.add("hide");

        setTimeout(() => {
            toast.remove();
        }, 400);
    }
}

setTimeout(() => {
    closeToast();
}, 5000);