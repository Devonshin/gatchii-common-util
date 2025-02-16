# gatchii-common-util
A Kotlin-based utility library for common tasks, ready to be integrated and distributed through **GitHub Packages**.
## **Features**
- Handy utility functions for everyday programming needs.
- Designed with Kotlin best practices.
- Full integration with GitHub Packages for easy publishing and usage.

## **Requirements**
- Kotlin version: **2**
- Java version: **21**
- Gradle version: **7.x+**

## **Setup**
### **1. Adding the Dependency**
### 
To include `gatchii-common-util` in your project, you need to add **GitHub Packages Repository** as a dependency repository in your `build.gradle` or `build.gradle.kts` file.
#### Groovy (build.gradle)
``` 
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Devonshin/gatchii-common-util")
    }
}

dependencies {
    implementation "com.gatchii:gatchii-common-util:{VERSION}"
}
```
#### Kotlin DSL (build.gradle.kts)
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Devonshin/gatchii-common-util")
    }
}

dependencies {
    implementation("com.gatchii:gatchii-common-util:{VERSION}")
}
```

## **Usage NOT YET WRITE**
After adding the dependency, you can start using the utilities from `gatchii-common-util`:
``` kotlin
import com.gatchii.utils.SomeUtility

fun main() {
    
}
```
## **Development**
### **Building the Project**
To build the project locally:
``` bash
./gradlew build
```
### **Running Tests**
To run the test suite:
``` bash
./gradlew test
```
Ensure you have the necessary credentials set up as mentioned in the "Authenticating with GitHub Packages" section.
## **Contributing**
Contributions are welcome!
Feel free to submit issues, suggestions, or pull requests to help make this project better.
1. Fork this repository.
2. Create a new branch with your feature/fix (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m "Add new feature"`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

## **License**
This project is licensed under the [MIT License](LICENSE).
### **Contact**
For support or inquiries, please contact [Devonshin](mailto:devonshin@example.com) via email or through GitHub Issues.
