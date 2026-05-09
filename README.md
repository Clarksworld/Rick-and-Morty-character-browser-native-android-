# Rick and Morty Character Browser

## Architecture
The project follows **Clean Architecture** principles combined with **MVVM (Model-View-ViewModel)**. This ensures a scalable and maintainable codebase by separating concerns into different layers:

- **Data Layer**: Responsible for data retrieval from both local (Room) and remote (Retrofit) sources. It implements the Repository pattern and handles data mapping and pagination using Paging 3's `RemoteMediator` for seamless offline support.
- **Domain Layer**: Contains the core business logic, models, and use cases. This layer is independent of any Android framework or external library.
- **UI Layer**: Built using Jetpack Compose, it handles the display of data and user interactions. ViewModels manage UI state and interact with the Domain layer.

## Key Libraries
- **Hilt**: For Dependency Injection.
- **Retrofit & Gson**: For network requests and JSON parsing.
- **Room**: For local caching and offline support.
- **Paging 3**: To handle paginated data from the API and local database.
- **Coil**: For image loading.
- **Navigation Compose**: For in-app navigation.
- **Kotlin Coroutines & Flow**: For asynchronous programming and reactive data streams.

## Features implemented
- **Character List**: Paginated list with image, name, species, and status.
- **Search & Filter**: Search by name and filter by status (Alive, Dead, Unknown). Optimized with `debounce`.
- **Offline Support**: Data is cached in Room. Users can browse characters even without an internet connection.
- **Character Detail**: Detailed view including the first 3 episodes they appeared in.
- **Error Handling**: Comprehensive handling of loading and error states with a retry mechanism.
- **State Management**: Using `StateFlow` and Paging's `LoadState`.

## Setup
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Sync Gradle and run the app.
