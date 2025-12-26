# royalrise

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


royalrise> alter table push_alarm
               add read_status boolean null
royalrise> alter table push_alarm
               alter column read_status set default false
royalrise> alter table authority
               add use_status boolean null