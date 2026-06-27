(function () {

    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const ICONS = {
        success : '✓',
        error   : '✕',
        warning : '⚠',
        info    : 'ℹ',
    };
    const LABELS = {
        success : 'Thành công',
        error   : 'Lỗi',
        warning : 'Cảnh báo',
        info    : 'Thông tin',
    };
    const AUTO_CLOSE_MS = 4000;
    const ANIM_MS       = 350;

    window.showToast = function (message, type = 'success') {
        type = ['success', 'error', 'warning', 'info'].includes(type) ? type : 'info';

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
      <div class="toast-content">
        <span class="toast-icon">${ICONS[type]}</span>
        <div class="toast-text">
          <strong>${LABELS[type]}</strong>
          <p>${message}</p>
        </div>
      </div>
      <button class="toast-close" aria-label="Đóng">✕</button>
    `;

        toast.querySelector('.toast-close')
            .addEventListener('click', () => dismissToast(toast));

        container.appendChild(toast);

        requestAnimationFrame(() => {
            requestAnimationFrame(() => toast.classList.add('toast-show'));
        });

        const timer = setTimeout(() => dismissToast(toast), AUTO_CLOSE_MS);

        toast.addEventListener('mouseenter', () => clearTimeout(timer));
        toast.addEventListener('mouseleave', () => {
            setTimeout(() => dismissToast(toast), AUTO_CLOSE_MS / 2);
        });
    };

    function dismissToast(toast) {
        if (!toast || toast.classList.contains('toast-hiding')) return;
        toast.classList.add('toast-hiding');
        setTimeout(() => toast.remove(), ANIM_MS);
    }
    window.closeToast = function () {
        document.querySelectorAll('.toast').forEach(t => dismissToast(t));
    };

    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('[data-toast]').forEach(el => {
            const type = el.getAttribute('data-toast');
            const msg  = el.getAttribute('data-msg') || el.textContent.trim();
            if (msg) showToast(msg, type);
        });
    });

})();