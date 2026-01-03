// Points d'intérêt statiques pour démonstration
export const interests = [
  // Paris
  {
    id: 1,
    name: 'École Primaire Jean Jaurès',
    type: 'École',
    rating: 4.5,
    distance: 0.3,
    lat: 48.857,
    lng: 2.373,
    icon: '🏫'
  },
  {
    id: 2,
    name: 'Carrefour Market',
    type: 'Supermarché',
    rating: 4.2,
    distance: 0.5,
    lat: 48.856,
    lng: 2.374,
    icon: '🛒'
  },
  {
    id: 3,
    name: 'Parc des Buttes-Chaumont',
    type: 'Parc',
    rating: 4.8,
    distance: 0.8,
    lat: 48.880,
    lng: 2.383,
    icon: '🌳'
  },
  {
    id: 4,
    name: 'Métro Belleville',
    type: 'Transport',
    rating: 4.0,
    distance: 0.4,
    lat: 48.872,
    lng: 2.383,
    icon: '🚇'
  },
  {
    id: 5,
    name: 'Hôpital Tenon',
    type: 'Santé',
    rating: 4.3,
    distance: 1.2,
    lat: 48.870,
    lng: 2.390,
    icon: '🏥'
  },
  {
    id: 6,
    name: 'Pharmacie Centrale',
    type: 'Santé',
    rating: 4.4,
    distance: 0.6,
    lat: 48.858,
    lng: 2.375,
    icon: '💊'
  },
  {
    id: 7,
    name: 'Gare de Lyon',
    type: 'Transport',
    rating: 4.1,
    distance: 1.5,
    lat: 48.844,
    lng: 2.373,
    icon: '🚂'
  },
  {
    id: 8,
    name: 'Monoprix République',
    type: 'Supermarché',
    rating: 4.3,
    distance: 0.9,
    lat: 48.867,
    lng: 2.363,
    icon: '🛒'
  },
  {
    id: 9,
    name: 'Collège Voltaire',
    type: 'École',
    rating: 4.6,
    distance: 0.7,
    lat: 48.861,
    lng: 2.368,
    icon: '🏫'
  },
  {
    id: 10,
    name: 'Parc de la Villette',
    type: 'Parc',
    rating: 4.7,
    distance: 1.3,
    lat: 48.894,
    lng: 2.387,
    icon: '🌳'
  },
  // Lyon
  {
    id: 11,
    name: 'Gare Part-Dieu',
    type: 'Transport',
    rating: 4.2,
    distance: 0.5,
    lat: 45.760,
    lng: 4.860,
    icon: '🚂'
  },
  {
    id: 12,
    name: 'Centre Commercial Part-Dieu',
    type: 'Commerce',
    rating: 4.5,
    distance: 0.6,
    lat: 45.764,
    lng: 4.858,
    icon: '🛍️'
  },
  {
    id: 13,
    name: 'École Internationale',
    type: 'École',
    rating: 4.8,
    distance: 0.8,
    lat: 45.768,
    lng: 4.840,
    icon: '🏫'
  },
  // Bordeaux
  {
    id: 14,
    name: 'Gare Saint-Jean',
    type: 'Transport',
    rating: 4.3,
    distance: 0.7,
    lat: 44.826,
    lng: -0.556,
    icon: '🚂'
  },
  {
    id: 15,
    name: 'Parc Bordelais',
    type: 'Parc',
    rating: 4.6,
    distance: 1.1,
    lat: 44.850,
    lng: -0.580,
    icon: '🌳'
  },
  // Marseille
  {
    id: 16,
    name: 'Gare Saint-Charles',
    type: 'Transport',
    rating: 4.1,
    distance: 0.9,
    lat: 43.303,
    lng: 5.380,
    icon: '🚂'
  },
  {
    id: 17,
    name: 'Plage des Catalans',
    type: 'Plage',
    rating: 4.4,
    distance: 1.2,
    lat: 43.248,
    lng: 5.375,
    icon: '🏖️'
  },
  {
    id: 18,
    name: 'Hôpital de la Timone',
    type: 'Santé',
    rating: 4.5,
    distance: 1.5,
    lat: 43.290,
    lng: 5.400,
    icon: '🏥'
  },
  // Nice
  {
    id: 19,
    name: 'Promenade des Anglais',
    type: 'Plage',
    rating: 4.7,
    distance: 0.5,
    lat: 43.695,
    lng: 7.265,
    icon: '🏖️'
  },
  {
    id: 20,
    name: 'Gare de Nice-Ville',
    type: 'Transport',
    rating: 4.2,
    distance: 0.8,
    lat: 43.704,
    lng: 7.262,
    icon: '🚂'
  },
  // Nantes
  {
    id: 21,
    name: 'Gare de Nantes',
    type: 'Transport',
    rating: 4.3,
    distance: 0.6,
    lat: 47.217,
    lng: -1.541,
    icon: '🚂'
  },
  {
    id: 22,
    name: 'Jardin des Plantes',
    type: 'Parc',
    rating: 4.6,
    distance: 0.9,
    lat: 47.214,
    lng: -1.550,
    icon: '🌳'
  },
  // Toulouse
  {
    id: 23,
    name: 'Gare Matabiau',
    type: 'Transport',
    rating: 4.2,
    distance: 0.7,
    lat: 43.611,
    lng: 1.454,
    icon: '🚂'
  },
  {
    id: 24,
    name: 'Université Toulouse III',
    type: 'École',
    rating: 4.4,
    distance: 0.5,
    lat: 43.604,
    lng: 1.444,
    icon: '🏫'
  },
  // Lille
  {
    id: 25,
    name: 'Gare Lille-Flandres',
    type: 'Transport',
    rating: 4.4,
    distance: 0.4,
    lat: 50.637,
    lng: 3.070,
    icon: '🚂'
  },
  {
    id: 26,
    name: 'Centre Commercial Euralille',
    type: 'Commerce',
    rating: 4.5,
    distance: 0.5,
    lat: 50.639,
    lng: 3.075,
    icon: '🛍️'
  },
  // Strasbourg
  {
    id: 27,
    name: 'Gare de Strasbourg',
    type: 'Transport',
    rating: 4.3,
    distance: 0.6,
    lat: 48.585,
    lng: 7.735,
    icon: '🚂'
  },
  {
    id: 28,
    name: 'Cathédrale Notre-Dame',
    type: 'Monument',
    rating: 4.8,
    distance: 0.8,
    lat: 48.582,
    lng: 7.751,
    icon: '⛪'
  },
  // Rennes
  {
    id: 29,
    name: 'Gare de Rennes',
    type: 'Transport',
    rating: 4.2,
    distance: 0.5,
    lat: 48.103,
    lng: -1.673,
    icon: '🚂'
  },
  {
    id: 30,
    name: 'Parc du Thabor',
    type: 'Parc',
    rating: 4.7,
    distance: 0.9,
    lat: 48.114,
    lng: -1.677,
    icon: '🌳'
  },
]

