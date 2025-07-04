<template>
  <header class="header">
    <div class="logo">MyApp</div>
    <nav class="nav">
      <a href="#" class="nav-link">Home</a>
      <a href="#" class="nav-link">About</a>
      <a href="#" class="nav-link">Contact</a>
    </nav>
  </header>
</template>

<script setup>
// 필요 시 로직 작성 가능
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #2c3e50;
  color: white;
  height: 60px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 15px;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 14px;
}

.nav-link:hover {
  text-decoration: underline;
}
</style>
