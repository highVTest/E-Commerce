import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@ConfigurationPropertiesScan("com.highv.ecommerce.infra.mongodb")
@EnableMongoRepositories(

    basePackages = ["com.highv.ecommerce.infra.mongodb"],
    mongoTemplateRef = "search-mongodb-template"
)
class MongoConfig {

    @Bean
    fun mongoTemplate(): MongoTemplate {
        val connectionString = "mongodb+srv://chadkim1021:<password>@cluster0807.e7xiz.mongodb.net/"
        val factory = SimpleMongoClientDatabaseFactory(connectionString)
        return MongoTemplate(factory)
    }
}