module.exports = {
  title: 'OAS Generator',
  description: 'This annotation processor generates an OpenAPI Specification based on static code analysis.',
  base: "/oas-generator/",
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      {
        text: 'Documentation',
        items: [
          { text: 'Installation', link: '/documentation/installation' },
          { text: 'Reference', link: '/documentation/reference' },
          { text: 'Extensibility', link: '/documentation/extensibility' },
        ]
      },
      { text: 'Github', link: 'https://github.com/chhorz/oas-generator' }
    ],
    sidebar: {
      '/documentation/': [
        ['installation', 'Installation'],
        ['reference', 'Reference'],
        ['extensibility', 'Extensibility'],
      ],
      // fallback
      '/': [
        ['', 'Home']
      ]
    }
  }
}
