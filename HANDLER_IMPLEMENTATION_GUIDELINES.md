# API Gateway Handler Rule Set

## 1. Handler Structure

### 1.1 Class Definition
- Implement `RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>`
- Use `@Log4j2` annotation for logging
- Include clear Javadoc describing the handler's purpose

### 1.2 Constants
- Define HTTP status codes as constants
- Use standard HTTP status codes from `java.net.HttpURLConnection` where possible
- Define a shared `ObjectMapper` with appropriate modules (e.g., `JavaTimeModule`)

### 1.3 Constructors
- Provide a default no-args constructor for AWS Lambda
- Include a dependency-injected constructor for testing
- Document both constructors with Javadoc

## 2. Request Handling

### 2.1 Main Handler Method
- Follow a 4-step pattern in the `handleRequest` method:
  1. Parse and validate input
  2. Execute business logic
  3. Create and return response
  4. Handle exceptions with appropriate error responses
- Log the beginning of request processing
- Use try-catch blocks for structured error handling

### 2.2 Input Parsing and Validation
- Create a dedicated method for parsing and validating input
- Check for null inputs first
- For POST/PUT requests, validate the request body
- For GET/DELETE requests, validate path parameters or query parameters
- Trim string inputs to handle whitespace
- Throw `IllegalArgumentException` with clear error messages for validation failures

### 2.3 Business Logic Execution
- Create a dedicated method for executing business logic
- Keep this method focused on calling the appropriate service method
- Log the key operation being performed
- Return the result from the service call

## 3. Response Handling

### 3.1 Success Response
- Create a dedicated method for success responses (`APIGatewayProxyResponseEvent`)
- Use HTTP 200 (OK) for successful operations
- Serialize the response object to JSON using the ObjectMapper
- Handle serialization exceptions

### 3.2 Error Response
- Create a dedicated method for error responses (`APIGatewayProxyResponseEvent`)
- Log the error message
- Use appropriate HTTP status codes (400 for client errors, 500 for server errors)
- Format error messages as JSON with a consistent structure

## 4. Request and Response Models

### 4.1 Request Models
- Create dedicated request classes for each handler only if the input is a complex object
- Use Lombok annotations for boilerplate code
- Place request classes in a `request` package under the handler package

### 4.2 Response Models
- Use domain models for responses where appropriate
- For complex responses, create dedicated response classes
- Place response classes in a `response` package under the handler package

## 5. Error Handling

### 5.1 Exception Types
- Use `IllegalArgumentException` for validation errors
- Use domain-specific exceptions for business logic errors
- Use generic exceptions for unexpected errors

### 5.2 Error Messages
- Provide clear, user-friendly error messages
- Include relevant context in error messages
- Avoid exposing sensitive information in error messages

## 6. Testing

### 6.1 Unit Tests
- Test each handler method independently
- Mock service dependencies
- Test both success and error scenarios
- Test input validation thoroughly