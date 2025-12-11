// src/utils/useMarkdown.ts
import MarkdownIt from 'markdown-it';
import DOMPurify from 'dompurify';

const md = new MarkdownIt({
  breaks: true,          // 줄바꿈 \n을 <br>로
  linkify: true,         // URL 자동 링크
});

export const renderMarkdownToHtml = (raw: string): string => {
  const dirty = md.render(raw);
  const clean = DOMPurify.sanitize(dirty);
  return clean;
};
