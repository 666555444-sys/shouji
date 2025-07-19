module.exports = {
    '/api': {
        target: 'http://localhost:48080', // 后端服务地址
        changeOrigin: true,
        pathRewrite: {
            '^/api': '' // 去掉请求路径中的 /api 前缀
        }
    }
} 