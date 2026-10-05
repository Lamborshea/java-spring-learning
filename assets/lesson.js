/* 教学小工具：可点击切换的 JS vs Java 代码块 */
(function() {
  // 给类比卡片添加点击切换
  document.querySelectorAll('.analogy').forEach(function(box) {
    const js = box.querySelector('.js-side');
    const java = box.querySelector('.java-side');
    if (!js || !java) return;
    const langTags = box.querySelectorAll('.lang-tag');
    langTags.forEach(function(t) {
      t.style.cursor = 'pointer';
      t.addEventListener('click', function() {
        if (t.parentElement.classList.contains('js-side')) {
          js.style.opacity = '1'; java.style.opacity = '0.3';
        } else {
          java.style.opacity = '1'; js.style.opacity = '0.3';
        }
      });
    });
  });

  // 进度标记 - 简单的 localStorage
  document.querySelectorAll('[data-lesson-id]').forEach(function(el) {
    const id = el.dataset.lessonId;
    if (localStorage.getItem('done-' + id) === '1') {
      el.classList.add('done');
    }
  });
})();
