import { createRouter, createWebHistory } from 'vue-router'

// Code splitting : charger les pages principales immédiatement
import Landing from '../views/Landing.vue'
import Home from '../views/Home.vue'
import PropertyDetail from '../views/PropertyDetail.vue'

// Lazy load les autres pages pour améliorer les performances
const Publish = () => import('../views/Publish.vue')
const About = () => import('../views/About.vue')
const Contact = () => import('../views/Contact.vue')

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
    path: '/property-slug/:slug',
    name: 'PropertyDetailBySlug',
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
    path: '/contact',
    name: 'Contact',
    component: Contact
  },
  {
    path: '/faq',
    name: 'FAQ',
    component: () => import('../views/FAQ.vue')
  },
  {
    path: '/guide-achat',
    name: 'GuideAchat',
    component: () => import('../views/GuideAchat.vue')
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
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('../views/BlogList.vue')
  },
  {
    path: '/blog/:slug',
    name: 'BlogPost',
    component: () => import('../views/BlogPostDetail.vue'),
    props: true
  },
  {
    path: '/blog/category/:category',
    name: 'BlogCategory',
    component: () => import('../views/BlogList.vue'),
    props: true
  },
  {
    path: '/blog/tag/:tag',
    name: 'BlogTag',
    component: () => import('../views/BlogList.vue'),
    props: true
  },
  {
    path: '/city/:city',
    name: 'City',
    component: () => import('../views/City.vue'),
    props: true
  },
  {
    path: '/agencies',
    name: 'Agencies',
    component: () => import('../views/Agencies.vue')
  },
  {
    path: '/agencies/:id',
    name: 'AgencyDetail',
    component: () => import('../views/AgencyDetail.vue'),
    props: true
  },
  {
    path: '/subscribe',
    name: 'Subscribe',
    component: () => import('../views/Subscribe.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
