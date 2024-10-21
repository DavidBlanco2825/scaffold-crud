package co.com.bancolombia.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class UserEntity {

    private String email;
    private String name;
    private Integer age;
    private String Status;

    public UserEntity() {
    }

    public UserEntity(String email, String name, Integer age, String status) {
        this.email = email;
        this.name = name;
        this.age = age;
        Status = status;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDbAttribute("age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
