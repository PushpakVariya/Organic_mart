document.addEventListener('DOMContentLoaded', function () {
  var loader = document.getElementById('loading-screen');
  if (loader) {
    setTimeout(function () {
      loader.style.opacity = '0';
      setTimeout(function () { loader.style.display = 'none'; }, 400);
    }, 300);
  }

  // Auto-dismiss alerts after 4s
  document.querySelectorAll('.alert-auto-dismiss').forEach(function (el) {
    setTimeout(function () {
      el.style.transition = 'opacity 0.5s';
      el.style.opacity = '0';
      setTimeout(function () { el.remove(); }, 500);
    }, 4000);
  });
});
