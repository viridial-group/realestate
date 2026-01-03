import { createRouter, createWebHistory } from 'vue-router'
import Landing from '../views/Landing.vue'
import Home from '../views/Home.vue'
import Publish from '../views/Publish.vue'
import About from '../views/About.vue'
import PropertyDetail from '../views/PropertyDetail.vue'

const routes = [
  {
    path: '/',
    name: 'Landing',
    component: Landing
  },
  {
    path: '/search',
    name: 'Search',
    component: Home
  },
  {
    path: '/property/:id',
    name: 'PropertyDetail',
    component: PropertyDetail,
    props: true
  },
  {
    path: '/publish',
    name: 'Publish',
    component: Publish
  },
  {
    path: '/about',
    name: 'About',
    component: About
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/Favorites.vue')
  },
  {
    path: '/compare',
    name: 'Compare',
    component: () => import('../views/Compare.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
