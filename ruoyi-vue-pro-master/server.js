const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const path = require('path');
const app = express();
const proxyConfig = require('./proxy.conf');

// 静态文件服务
app.use(express.static(__dirname));

// 配置API代理
Object.keys(proxyConfig).forEach(context => {
  const options = proxyConfig[context];
  app.use(context, createProxyMiddleware(options));
});

// 所有路由返回index.html（单页应用支持）
app.get('*', (req, res) => {
  if (!req.path.startsWith('/api')) {
    res.sendFile(path.join(__dirname, 'index.html'));
  }
});

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
  console.log(`访问登录页面: http://localhost:${PORT}/login.html`);
  console.log(`访问首页: http://localhost:${PORT}/index.html`);
}); 