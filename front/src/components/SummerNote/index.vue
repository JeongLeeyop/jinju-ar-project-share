<template>
  <div class="summernote"/>
</template>

<script lang="ts">
import { upload } from '@/api/attached-file';
import $ from 'jquery';
import 'popper.js';

import CodeMirror from 'codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/mode/xml/xml';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/mode/css/css';
import 'codemirror/theme/monokai.css';

import 'summernote/dist/summernote-lite';
import 'summernote/dist/summernote-lite.css';

import {
  Component,
  Vue,
  Prop,
  Watch,
} from 'vue-property-decorator';

@Component({
  name: 'SummernoteEditor',
})

export default class extends Vue {
  @Prop({ required: false }) private contents!: string;

  @Prop({ default: 300 }) private height!: number;

  private initCount = 0;

  mounted() {
    this.setLanguage().then(async () => {
      await this.initEditor();
      if (this.initCount === 0) {
        setTimeout(() => {
          ($(this.$el) as any).summernote('code', this.contents);
        }, 1000);
      }
    });
  }

  private getToolbar() {
    const toolbar = [
      ['style', ['style']],  // H1~H6, 인용구, 코드
      ['font', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],  // 굵게, 기울임, 밑줄, 취소선, 서식지우기
      ['fontname', ['fontname']],  // 폰트
      ['fontsize', ['fontsize']],  // 글자 크기
      ['color', ['color']],  // 글자색, 배경색
      ['para', ['ul', 'ol', 'paragraph']],  // 글머리기호, 번호매기기, 문단정렬
      ['height', ['height']],  // 줄간격
      ['table', ['table']],  // 표
      ['insert', ['link', 'hr']],  // 링크, 수평선
      // ['insert', ['picture']],  // 이미지는 주석 처리됨
      ['view', ['codeview', 'help']],  // 코드보기, 도움말
    ];
    return toolbar;
  }

  private async initEditor() {
    /* eslint-disable */
    const that = this;
    ($(that.$el) as any).summernote({
      lang: 'ko-KR',
      height: that.height,
      codemirror: { // codemirror options
        CodeMirrorConstructor: CodeMirror,
        theme: 'monokai',
        matchBrackets: true,
        tabSize: 2,
        lineNumbers: true,
        line: true,
      },
      codeviewFilter: false,
      codeviewIframeFilter: false,
      toolbar: that.getToolbar(),
      callbacks: {
        onImageUpload: function(files: any) {
          const formData = new FormData();
          const attachedFile = files[0];
          formData.append('file', attachedFile);
          if (!attachedFile.type.startsWith('image')) {
            alert('이미지 파일만 업로드 가능합니다.');
            return;
          }
          if (attachedFile.size > 3 * 1024 * 1024) {
            alert('최대 3MB까지 업로드 가능합니다.');
            return;
          }
          upload('post', formData).then((res) => {
            ($(that.$el) as any).summernote('insertImage', '/api/attached-file/'+ res.data.uid);
          });
        },
        onChange: function(contents:string): void {
          that.$emit('change', contents);
        },
      },
    });
    /* eslint-enable */
  }

  private setLanguage() {
    /* eslint-disable */
    return new Promise((resolve, reject) => {
      const lang = ($ as any).summernote.lang;
      $.extend(lang, {
        'ko-KR': {
          font: {
            bold: '굵게',
            italic: '기울임꼴',
            underline: '밑줄',
            clear: '서식 지우기',
            height: '줄 간격',
            name: '글꼴',
            superscript: '위 첨자',
            subscript: '아래 첨자',
            strikethrough: '취소선',
            size: '글자 크기',
          },
          image: {
            image: '그림',
            insert: '그림 삽입',
            resizeFull: '100% 크기로 변경',
            resizeHalf: '50% 크기로 변경',
            resizeQuarter: '25% 크기로 변경',
            resizeNone: '원본 크기',
            floatLeft: '왼쪽 정렬',
            floatRight: '오른쪽 정렬',
            floatNone: '정렬하지 않음',
            shapeRounded: '스타일: 둥근 모서리',
            shapeCircle: '스타일: 원형',
            shapeThumbnail: '스타일: 액자',
            shapeNone: '스타일: 없음',
            dragImageHere: '텍스트 혹은 사진을 이곳으로 끌어오세요',
            dropImage: '텍스트 혹은 사진을 내려놓으세요',
            selectFromFiles: '파일 선택',
            maximumFileSize: '최대 파일 크기',
            maximumFileSizeError: '최대 파일 크기를 초과했습니다.',
            url: '사진 URL',
            remove: '사진 삭제',
            original: '원본',
          },
          video: {
            video: '동영상',
            videoLink: '동영상 링크',
            insert: '동영상 삽입',
            url: '동영상 URL',
            providers: '(YouTube, Vimeo, Vine, Instagram, DailyMotion, Youku 사용 가능)',
          },
          link: {
            link: '링크',
            insert: '링크 삽입',
            unlink: '링크 삭제',
            edit: '수정',
            textToDisplay: '링크에 표시할 내용',
            url: '이동할 URL',
            openInNewWindow: '새창으로 열기',
          },
          table: {
            table: '표',
            addRowAbove: '위에 행 삽입',
            addRowBelow: '아래에 행 삽입',
            addColLeft: '왼쪽에 열 삽입',
            addColRight: '오른쪽에 열 삽입',
            delRow: '행 지우기',
            delCol: '열 지우기',
            delTable: '표 삭제',
          },
          hr: {
            insert: '구분선 삽입',
          },
          style: {
            style: '스타일',
            p: '본문',
            blockquote: '인용구',
            pre: '코드',
            h1: '제목 1',
            h2: '제목 2',
            h3: '제목 3',
            h4: '제목 4',
            h5: '제목 5',
            h6: '제목 6',
          },
          lists: {
            unordered: '글머리 기호',
            ordered: '번호 매기기',
          },
          options: {
            help: '도움말',
            fullscreen: '전체 화면',
            codeview: '코드 보기',
          },
          paragraph: {
            paragraph: '문단 정렬',
            outdent: '내어쓰기',
            indent: '들여쓰기',
            left: '왼쪽 정렬',
            center: '가운데 정렬',
            right: '오른쪽 정렬',
            justify: '양쪽 정렬',
          },
          color: {
            recent: '마지막으로 사용한 색',
            more: '다른 색 선택',
            background: '배경색',
            foreground: '글자색',
            transparent: '투명',
            setTransparent: '투명으로 설정',
            reset: '취소',
            resetToDefault: '기본값으로 설정',
            cpSelect: '고르다',
          },
          shortcut: {
            shortcuts: '키보드 단축키',
            close: '닫기',
            textFormatting: '글자 스타일 적용',
            action: '기능',
            paragraphFormatting: '문단 스타일 적용',
            documentStyle: '문서 스타일 적용',
            extraKeys: '추가 키',
          },
          help: {
            insertParagraph: '문단 삽입',
            undo: '마지막 명령 취소',
            redo: '마지막 명령 재실행',
            tab: '탭',
            untab: '탭 제거',
            bold: '굵은 글자로 설정',
            italic: '기울임꼴 글자로 설정',
            underline: '밑줄 글자로 설정',
            strikethrough: '취소선 글자로 설정',
            removeFormat: '서식 삭제',
            justifyLeft: '왼쪽 정렬하기',
            justifyCenter: '가운데 정렬하기',
            justifyRight: '오른쪽 정렬하기',
            justifyFull: '좌우채움 정렬하기',
            insertUnorderedList: '글머리 기호 켜고 끄기',
            insertOrderedList: '번호 매기기 켜고 끄기',
            outdent: '현재 문단 내어쓰기',
            indent: '현재 문단 들여쓰기',
            formatPara: '현재 블록의 포맷을 문단(P)으로 변경',
            formatH1: '현재 블록의 포맷을 제목1(H1)로 변경',
            formatH2: '현재 블록의 포맷을 제목2(H2)로 변경',
            formatH3: '현재 블록의 포맷을 제목3(H3)로 변경',
            formatH4: '현재 블록의 포맷을 제목4(H4)로 변경',
            formatH5: '현재 블록의 포맷을 제목5(H5)로 변경',
            formatH6: '현재 블록의 포맷을 제목6(H6)로 변경',
            insertHorizontalRule: '구분선 삽입',
          },
          history: {
            undo: '실행 취소',
            redo: '재실행',
          },
          specialChar: {
            specialChar: '특수문자',
            select: '특수문자를 선택하세요',
          },
        },
      });
      resolve('');
    });
    /* eslint-enable */
  }
}
</script>

<style lang="scss">
.CodeMirror-code {
  line-height: 1.5 !important;
}
.note-modal-footer {height:60px;}

/* Summernote 에디터 내부 콘텐츠 스타일 */
.note-editable {
  font-family: Pretendard, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif !important;
  font-size: 16px !important;
  line-height: 1.6 !important;
  color: #222 !important;

  /* 제목 태그 (H1-H6) */
  h1 {
    font-size: 32px !important;
    font-weight: 700 !important;
    margin: 24px 0 16px 0 !important;
    line-height: 1.3 !important;
    color: #222 !important;
  }

  h2 {
    font-size: 28px !important;
    font-weight: 700 !important;
    margin: 22px 0 14px 0 !important;
    line-height: 1.3 !important;
    color: #222 !important;
  }

  h3 {
    font-size: 24px !important;
    font-weight: 600 !important;
    margin: 20px 0 12px 0 !important;
    line-height: 1.4 !important;
    color: #222 !important;
  }

  h4 {
    font-size: 20px !important;
    font-weight: 600 !important;
    margin: 18px 0 10px 0 !important;
    line-height: 1.4 !important;
    color: #333 !important;
  }

  h5 {
    font-size: 18px !important;
    font-weight: 600 !important;
    margin: 16px 0 8px 0 !important;
    line-height: 1.5 !important;
    color: #333 !important;
  }

  h6 {
    font-size: 16px !important;
    font-weight: 600 !important;
    margin: 14px 0 8px 0 !important;
    line-height: 1.5 !important;
    color: #444 !important;
  }

  /* 단락 */
  p {
    margin: 0 0 12px 0 !important;
    line-height: 1.6 !important;
  }

  /* 강조 (굵게, 기울임, 밑줄, 취소선) */
  strong, b {
    font-weight: 700 !important;
  }

  em, i {
    font-style: italic !important;
  }

  u {
    text-decoration: underline !important;
  }

  s, strike {
    text-decoration: line-through !important;
  }

  /* 인용구 */
  blockquote {
    margin: 16px 0 !important;
    padding: 12px 20px !important;
    border-left: 4px solid #073DFF !important;
    background-color: #F5F7FA !important;
    color: #555 !important;
    font-style: italic !important;
  }

  /* 코드 */
  code {
    padding: 2px 6px !important;
    background-color: #F5F5F5 !important;
    border: 1px solid #E8E8E8 !important;
    border-radius: 3px !important;
    font-family: 'Courier New', Courier, monospace !important;
    font-size: 14px !important;
    color: #E83E8C !important;
  }

  pre {
    margin: 16px 0 !important;
    padding: 16px !important;
    background-color: #F5F5F5 !important;
    border: 1px solid #E8E8E8 !important;
    border-radius: 4px !important;
    overflow-x: auto !important;
    font-family: 'Courier New', Courier, monospace !important;
    font-size: 14px !important;
    line-height: 1.5 !important;
    color: #333 !important;

    code {
      padding: 0 !important;
      background: none !important;
      border: none !important;
      color: inherit !important;
    }
  }

  /* 목록 (글머리 기호, 번호 매기기) */
  ul {
    margin: 12px 0 !important;
    padding-left: 24px !important;
    list-style-type: disc !important;

    ul {
      list-style-type: circle !important;

      ul {
        list-style-type: square !important;
      }
    }
  }

  ol {
    margin: 12px 0 !important;
    padding-left: 24px !important;
    list-style-type: decimal !important;
  }

  li {
    margin: 6px 0 !important;
    line-height: 1.6 !important;
  }

  /* 링크 */
  a {
    color: #073DFF !important;
    text-decoration: underline !important;
    transition: color 0.2s !important;

    &:hover {
      color: #0528A3 !important;
    }
  }

  /* 표 (Table) */
  table {
    width: 100% !important;
    margin: 16px 0 !important;
    border-collapse: collapse !important;
    border: 1px solid #E8E8E8 !important;

    th, td {
      padding: 10px 12px !important;
      border: 1px solid #E8E8E8 !important;
      text-align: left !important;
    }

    th {
      background-color: #F5F7FA !important;
      font-weight: 600 !important;
      color: #333 !important;
    }

    tr:nth-child(even) {
      background-color: #FAFAFA !important;
    }
  }

  /* 이미지 */
  img {
    max-width: 100% !important;
    height: auto !important;
    margin: 12px 0 !important;
    border-radius: 4px !important;
  }

  /* 수평선 */
  hr {
    margin: 20px 0 !important;
    border: none !important;
    border-top: 1px solid #E8E8E8 !important;
  }

  /* 첫 번째/마지막 자식 요소 마진 제거 */
  > *:first-child {
    margin-top: 0 !important;
  }

  > *:last-child {
    margin-bottom: 0 !important;
  }
}
</style>
