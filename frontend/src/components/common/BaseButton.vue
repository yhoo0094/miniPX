<template>
    <button class="base-button">
      <slot />
    </button>
  </template>
  
  <style scoped>
  .base-button {
    width: 100px;
    height: 60px;
    font-size: 18px;
    background-color: white;
    border: 2px solid #0c254d;
    cursor: pointer;
  }
  </style>
  