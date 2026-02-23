<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('notes')
</script>

<template>
  <div class="profile">
    <div class="container">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的笔记" name="notes" />
        <el-tab-pane label="我的问题" name="questions" />
        <el-tab-pane label="我的回答" name="answers" />
      </el-tabs>
    </div>
  </div>
</template>

<style scoped>
.profile {
  padding: 20px;
}
.container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
