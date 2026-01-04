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
    path: '/help',
    name: 'Help',
    component: () => import('../views/Help.vue')
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
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/my-properties',
    name: 'MyProperties',
    component: () => import('../views/MyProperties.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/my-properties/new',
    name: 'PropertyFormNew',
    component: () => import('../views/PropertyForm.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/my-properties/:id/edit',
    name: 'PropertyFormEdit',
    component: () => import('../views/PropertyForm.vue'),
    props: true,
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/my-properties/:id',
    name: 'MyPropertyDetail',
    component: () => import('../views/MyPropertyDetail.vue'),
    props: true,
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('../views/ResetPassword.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/my-messages',
    name: 'MyMessages',
    component: () => import('../views/MyMessages.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  },
  {
    path: '/profile/settings',
    name: 'ProfileSettings',
    component: () => import('../views/Settings.vue'),
    meta: { requiresAuth: true, layout: 'user' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Guard de route pour l'authentification
router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    // Importer le store d'authentification
    const { useAuthStore } = await import('@viridial/shared')
    const authStore = useAuthStore()
    
    // Vérifier l'authentification
    await authStore.checkAuth()
    
    if (!authStore.isAuthenticated) {
      // Rediriger vers la page de connexion avec la route de retour
      next({
        name: 'Login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
