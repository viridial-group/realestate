import { ref, computed, onMounted, watch } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet.heat'

export function useFilters() {
  const query = ref('')
  const selectedType = ref('Tous')
  const selectedStatus = ref('Tous')
  const priceRange = ref('')
  const surfaceMin = ref('')
  const roomsMin = ref('')
  const sortBy = ref('')
  const currentPage = ref(1)
  const perPage = 2

  const ads = ref([
    {id:1,title:"Appartement moderne",city:"Casablanca",neighborhood:"Maarif",price:245000,surface:78,rooms:3,type:"Appartement",status:"Disponible",rating:4.5,services:["Balcon","Parking"],image:"https://images.unsplash.com/photo-1560448204-e02f11c3d0e2",description:"Appartement lumineux proche commodités."},
    {id:2,title:"Villa avec piscine",city:"Rabat",neighborhood:"Souissi",price:1250000,surface:320,rooms:6,type:"Villa",status:"Vendu",rating:4.8,services:["Piscine","Jardin"],image:"https://images.unsplash.com/photo-1600585154340-be6161a56a0c",description:"Grande villa haut standing."},
    {id:3,title:"Studio meublé",city:"Marrakech",neighborhood:"Guéliz",price:120000,surface:45,rooms:1,type:"Studio",status:"Loué",rating:3.9,services:["Meublé"],image:"https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",description:"Studio idéal investissement locatif."}
  ])

  const pointsOfInterest = ref([
    {id:1,name:"École Jean",type:"École",rating:5,distance:0.3,lat:33.5731,lng:-7.5898},
    {id:2,name:"Supermarché Carrefour",type:"Supermarché",rating:4,distance:0.6,lat:33.5735,lng:-7.5850},
    {id:3,name:"Parc Central",type:"Parc",rating:4.5,distance:0.9,lat:33.5700,lng:-7.5900}
  ])

  const filteredAds = computed(()=>{
    let res = ads.value.filter(ad=>{
      const q=query.value.toLowerCase()
      const matchQuery = !query.value || ad.title.toLowerCase().includes(q) || ad.city.toLowerCase().includes(q) || ad.neighborhood.toLowerCase().includes(q)
      const matchType = selectedType.value==="Tous" || ad.type===selectedType.value
      const matchStatus = selectedStatus.value==="Tous" || ad.status===selectedStatus.value
      const matchPrice = !priceRange.value || (()=>{const [min,max]=priceRange.value.split("-").map(Number); return ad.price>=min && ad.price<=max})()
      const matchSurface = !surfaceMin.value || ad.surface>=Number(surfaceMin.value)
      const matchRooms = !roomsMin.value || ad.rooms>=Number(roomsMin.value)
      return matchQuery && matchType && matchStatus && matchPrice && matchSurface && matchRooms
    })
    if(sortBy.value==="rating") res.sort((a,b)=>b.rating-a.rating)
    else if(sortBy.value==="priceAsc") res.sort((a,b)=>a.price-b.price)
    else if(sortBy.value==="priceDesc") res.sort((a,b)=>b.price-a.price)
    return res
  })

  const totalPages = computed(()=>Math.ceil(filteredAds.value.length/perPage))
  const paginatedAds = computed(()=>filteredAds.value.slice((currentPage.value-1)*perPage,(currentPage.value-1)*perPage+perPage))

  const topNeighborhoods = computed(()=>{
    const map = {}
    filteredAds.value.forEach(a=>{ map[a.neighborhood]=(map[a.neighborhood]||0)+1 })
    return Object.keys(map).map(k=>({name:k,count:map[k]})).sort((a,b)=>b.count-a.count).slice(0,5)
  })

  const sortedPOI = computed(()=>pointsOfInterest.value.sort((a,b)=>b.rating-a.rating))

  const filterByNeighborhood = name => { query.value=name }

  const prevPage = ()=>{ if(currentPage.value>1) currentPage.value-- }
  const nextPage = ()=>{ if(currentPage.value<totalPages.value) currentPage.value++ }

  const viewDetails = ad=>alert(`Voir détails ${ad.title}`)
  const addToFavorites = ad=>alert(`${ad.title} ajouté aux favoris`)
  const contact = ad=>alert(`Contacter pour ${ad.title}`)
  const zoomPOI = poi => { console.log(`Zoom POI ${poi.name}`) }

  return { query, selectedType, selectedStatus, priceRange, surfaceMin, roomsMin, sortBy, filteredAds, paginatedAds, currentPage, totalPages, prevPage, nextPage, topNeighborhoods, filterByNeighborhood, pointsOfInterest, sortedPOI, viewDetails, addToFavorites, contact, zoomPOI }
}
