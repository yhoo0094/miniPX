export {};

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_API_TIMEOUT?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare global {
  interface HTMLElement {
    __ClickOutsideHandler__?: (event: MouseEvent) => void;
  }
}