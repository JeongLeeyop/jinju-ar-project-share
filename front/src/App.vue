<template>
  <div id="app">
    <div>
      <router-view class="app-wrap" />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component({
  name: 'App',
  components: {
  },
})

export default class extends Vue {
}
</script>

<style lang="scss">
@import './assets/css/default.css';
@import './assets/css/style.css';
@import './assets/css/jinjuAr.scss';

.app-wrap{
  /* display: flex; */
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}

/* Global Element UI Message z-index fix */
.el-message {
  z-index: 9999 !important;
  top: 20px !important;
}

.el-message-box {
  z-index: 9999 !important;
}
</style>
