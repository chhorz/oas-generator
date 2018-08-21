module.exports = {
  title: 'OAS Generator',
  description: 'This annotation processor generates an OpenAPI Specification based on static code analysis.',
  base: "/oas-generator/",
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Documentation', link: '/documentation/' },
      { text: 'Github', link: 'https://github.com/chhorz/oas-generator' }
    ],
    sidebar: {
      '/documentation/': [
        ['', 'Documentation'],
        ['installation', 'Installation'],
        ['reference', 'Reference']
      ],
      // fallback
      '/': [
        ['', 'Home']
      ]
    },
    lastUpdated: 'Last Updated'
  }
}
