# LangChain4j Training Course Slides

Slidev presentation for the LangChain4j training course.

## Quick Start

1. **Navigate to slides directory**:
   ```bash
   cd slides
   ```

2. **Install dependencies** (requires Node.js):
   ```bash
   npm install
   ```

3. **Start the slide server** (opens in browser):
   ```bash
   npm run dev
   ```

4. **Navigate slides**:
   - **Next**: Space, Right Arrow, or Click
   - **Previous**: Left Arrow
   - **Overview**: Press 'o'
   - **Presenter mode**: Visit http://localhost:3030/presenter
   - **Dark/Light mode**: Press 'd'

## Export Options

```bash
# Export to PDF
npm run export-pdf

# Export to PNG images
npm run export

# Build static site
npm run build
```

## Slidev Features Used

- **Code highlighting**: Step through code blocks with clicks
- **Animations**: Elements appear on click using `v-click`
- **Layouts**: two-cols, center, image-right, fact
- **Mermaid diagrams**: For flowcharts
- **Icons**: Carbon icons (e.g., `<carbon:arrow-right />`)
- **Speaker notes**: Add notes in frontmatter or with `<!-- -->` 

## Editing Tips

1. **New slide**: Add `---` on its own line
2. **Code blocks**: Use ` ```java {line-numbers} ` for highlighting
3. **Transitions**: Set with `transition: fade-out` in frontmatter
4. **Themes**: Change `theme: seriph` to others (default, apple-basic, etc.)
5. **Live reload**: Changes appear instantly while server runs

## Resources

- [Slidev Documentation](https://sli.dev/)
- [Available Themes](https://sli.dev/themes/gallery.html)
- [Icon Sets](https://sli.dev/guide/syntax#icons)