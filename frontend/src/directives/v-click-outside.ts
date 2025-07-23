export default {
  beforeMount(el: HTMLElement, binding: any) {
    el.__ClickOutsideHandler__ = (event: MouseEvent) => {
      if (!(el === event.target || el.contains(event.target as Node))) {
        binding.value(event);
      }
    };
    document.addEventListener('mousedown', el.__ClickOutsideHandler__);
  },
  unmounted(el: HTMLElement) {
    document.removeEventListener('mousedown', el.__ClickOutsideHandler__);
    delete el.__ClickOutsideHandler__;
  }
};