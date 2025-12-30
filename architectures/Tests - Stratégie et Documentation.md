# 🧪 Tests - Stratégie et Documentation

**Date de création :** Décembre 2024

---

## 📋 Vue d'ensemble

Ce document décrit la stratégie de tests pour le projet SaaS Immobilier, incluant les tests unitaires, d'intégration, E2E et de performance.

---

## 🎯 Objectifs de Tests

- **Couverture de code** : > 80% pour tous les services
- **Fiabilité** : Tous les tests doivent passer avant déploiement
- **Performance** : Tests de charge pour valider la scalabilité
- **Sécurité** : Tests de sécurité pour valider les contrôles d'accès

---

## 📊 Pyramide de Tests

```
        /\
       /E2E\          (10%) - Tests End-to-End
      /------\
     /Integration\    (30%) - Tests d'Intégration
    /------------\
   /   Unitaires   \  (60%) - Tests Unitaires
  /----------------\
```

---

## 🔧 Tests Unitaires

### Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **JUnit 5** | 5.10.x | Framework de tests |
| **Mockito** | 5.7.x | Mocking |
| **AssertJ** | 3.24.x | Assertions fluides |
| **JaCoCo** | 0.8.11 | Couverture de code |

### Configuration Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Exemples de Tests

#### Test de Service

```java
@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {
    
    @Mock
    private PropertyRepository propertyRepository;
    
    @Mock
    private IdentityService identityService;
    
    @InjectMocks
    private PropertyService propertyService;
    
    @Test
    void shouldCreatePropertyWhenUserHasPermission() {
        // Given
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setTitle("Test Property");
        User user = new User();
        user.setId(1L);
        
        when(identityService.hasPermission(user, "create_property"))
            .thenReturn(true);
        when(propertyRepository.save(any(Property.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Property result = propertyService.createProperty(propertyDTO, user);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Property");
        verify(propertyRepository).save(any(Property.class));
    }
}
```

#### Test de Repository

```java
@DataJpaTest
class PropertyRepositoryTest {
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void shouldFindPropertiesByOrganizationId() {
        // Given
        Organization org = new Organization();
        org.setId(1L);
        entityManager.persist(org);
        
        Property property = new Property();
        property.setOrganizationId(1L);
        entityManager.persist(property);
        
        // When
        List<Property> result = propertyRepository.findByOrganizationId(1L);
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrganizationId()).isEqualTo(1L);
    }
}
```

#### Test de Controller

```java
@WebMvcTest(PropertyController.class)
class PropertyControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PropertyService propertyService;
    
    @Test
    void shouldReturnPropertyWhenFound() throws Exception {
        // Given
        Property property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");
        
        when(propertyService.findById(1L)).thenReturn(property);
        
        // When & Then
        mockMvc.perform(get("/api/properties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Property"));
    }
}
```

### Couverture de Code

**Objectif :** > 80% pour tous les services

```bash
# Générer le rapport de couverture
mvn clean test jacoco:report

# Vérifier la couverture
mvn jacoco:check
```

---

## 🔗 Tests d'Intégration

### Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **Testcontainers** | 1.19.x | Conteneurs Docker pour tests |
| **Spring Boot Test** | 3.3.1 | Tests d'intégration Spring |
| **WireMock** | 3.0.x | Mocking de services externes |

### Configuration Testcontainers

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>redis</artifactId>
    <scope>test</scope>
</dependency>
```

### Exemple de Test d'Intégration

```java
@SpringBootTest
@Testcontainers
class PropertyServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.2-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2.4-alpine")
            .withExposedPorts(6379);
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:3.6.1"));
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
    
    @Test
    void shouldCreateAndRetrieveProperty() {
        // Given
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setTitle("Integration Test Property");
        
        // When
        Property created = propertyService.createProperty(propertyDTO, testUser);
        Property retrieved = propertyService.findById(created.getId());
        
        // Then
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTitle()).isEqualTo("Integration Test Property");
    }
}
```

### Tests Cross-Services

```java
@SpringBootTest
@Testcontainers
class PropertyWorkflowIntegrationTest {
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private WorkflowService workflowService;
    
    @Test
    void shouldTriggerWorkflowWhenPropertyCreated() {
        // Given
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setTitle("Property with Workflow");
        
        // When
        Property property = propertyService.createProperty(propertyDTO, testUser);
        
        // Then
        List<Task> tasks = workflowService.getTasksByPropertyId(property.getId());
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
    }
}
```

---

## 🌐 Tests E2E (End-to-End)

### Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **Cypress** | 13.x | Tests E2E frontend |
| **Playwright** | 1.40.x | Alternative E2E |
| **REST Assured** | 5.4.x | Tests API E2E |

### Exemple Cypress

```javascript
describe('Property Management E2E', () => {
  beforeEach(() => {
    cy.login('test@example.com', 'password');
  });

  it('should create a new property', () => {
    cy.visit('/properties');
    cy.get('[data-testid="create-property-btn"]').click();
    cy.get('[data-testid="property-title"]').type('E2E Test Property');
    cy.get('[data-testid="property-price"]').type('100000');
    cy.get('[data-testid="save-property-btn"]').click();
    
    cy.url().should('include', '/properties/');
    cy.get('[data-testid="property-title"]').should('contain', 'E2E Test Property');
  });
});
```

### Exemple REST Assured

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PropertyE2ETest {
    
    @LocalServerPort
    private int port;
    
    @Test
    void shouldCompletePropertyWorkflow() {
        // 1. Authenticate
        String token = given()
            .port(port)
            .body("{\"email\":\"test@example.com\",\"password\":\"password\"}")
            .contentType(ContentType.JSON)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("token");
        
        // 2. Create Property
        Long propertyId = given()
            .port(port)
            .header("Authorization", "Bearer " + token)
            .body("{\"title\":\"E2E Property\",\"price\":100000}")
            .contentType(ContentType.JSON)
            .when()
            .post("/api/properties")
            .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // 3. Verify Property
        given()
            .port(port)
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/api/properties/" + propertyId)
            .then()
            .statusCode(200)
            .body("title", equalTo("E2E Property"));
    }
}
```

---

## ⚡ Tests de Performance

### Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **JMeter** | 5.6.x | Tests de charge |
| **Gatling** | 3.10.x | Tests de performance |
| **K6** | Latest | Tests de performance moderne |

### Exemple Gatling

```scala
class PropertyLoadTest extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .authorizationHeader("Bearer ${token}")
  
  val scn = scenario("Property Load Test")
    .exec(http("Get Properties")
      .get("/api/properties")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Create Property")
      .post("/api/properties")
      .body(StringBody("""{"title":"Load Test Property","price":100000}"""))
      .check(status.is(201)))
  
  setUp(
    scn.inject(
      rampUsers(100) during (60 seconds),
      constantUsersPerSec(10) during (300 seconds)
    )
  ).protocols(httpProtocol)
}
```

### Métriques à Surveiller

- **Response Time** : < 200ms (p95)
- **Throughput** : > 1000 req/s
- **Error Rate** : < 0.1%
- **CPU Usage** : < 70%
- **Memory Usage** : < 80%

---

## 🔒 Tests de Sécurité

### Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **OWASP ZAP** | Latest | Tests de sécurité |
| **Snyk** | Latest | Scan de vulnérabilités |
| **Spring Security Test** | 6.3.1 | Tests de sécurité Spring |

### Exemple Test de Sécurité

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/properties"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    void shouldRejectRequestWithoutPermission() throws Exception {
        String token = getTokenForUser("user@example.com");
        
        mockMvc.perform(delete("/api/properties/1")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }
    
    @Test
    void shouldAcceptRequestWithPermission() throws Exception {
        String token = getTokenForUser("admin@example.com");
        
        mockMvc.perform(get("/api/properties")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
```

---

## 📊 Stratégie de Tests par Service

### Identity & Auth Service
- [ ] Tests unitaires : UserService, RoleService, PermissionService
- [ ] Tests d'intégration : Authentication, Authorization
- [ ] Tests de sécurité : JWT, OAuth2, RBAC, ACL

### Property Service
- [ ] Tests unitaires : PropertyService, PropertyRepository
- [ ] Tests d'intégration : CRUD, Workflow, Document association
- [ ] Tests E2E : Scénarios complets de création/modification

### Workflow Service
- [ ] Tests unitaires : WorkflowEngine, TaskService
- [ ] Tests d'intégration : Multi-étapes, Approbations
- [ ] Tests E2E : Workflows complets

---

## ✅ Checklist de Tests

### Avant chaque Commit
- [ ] Tous les tests unitaires passent
- [ ] Couverture de code > 80%
- [ ] Aucun test en échec

### Avant chaque Merge
- [ ] Tous les tests unitaires passent
- [ ] Tous les tests d'intégration passent
- [ ] Tests E2E critiques passent
- [ ] Code review effectué

### Avant chaque Déploiement
- [ ] Tous les tests passent
- [ ] Tests de performance validés
- [ ] Tests de sécurité validés
- [ ] Documentation à jour

---

## 📝 Commandes Utiles

```bash
# Exécuter tous les tests
mvn clean test

# Exécuter les tests avec couverture
mvn clean test jacoco:report

# Exécuter uniquement les tests unitaires
mvn test -Dtest=*Test

# Exécuter uniquement les tests d'intégration
mvn test -Dtest=*IntegrationTest

# Exécuter les tests E2E
npm run test:e2e

# Générer le rapport de couverture
mvn jacoco:report
open target/site/jacoco/index.html
```

---

**Dernière mise à jour :** Décembre 2024

