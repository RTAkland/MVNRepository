import {defineConfig} from 'vitepress'

// https://vitepress.vuejs.org/config/app-configs
export default defineConfig({
    title: "MVNRepository",
    description: "MVN私服",
    vite: {
        resolve: {
            alias: {
                '@components': 'docs/components'
            },
        },
    },
})
