# TeachMeSkills_Final_Assignment

## General Description:

This project is designed for processing financial documents (invoices, orders, and receipts), analyzing text files, calculating total turnover, and generating reports. The program supports two-factor authentication (TFA) using OTP and QR codes and integrates with Amazon S3 for uploading statistics.

## Main Features:

- Authentication using login, password, and OTP.
- Processing text files (invoices, orders, bills).
- Generating turnover statistics.
- Creating reports and uploading them to Amazon S3 cloud storage.
- Logging errors and process-related information.

## Project Structure:

### authentication: Package for authentication and two-factor authentication.
- AuthenticationService: Service for login and OTP verification.
- TFAUtils: Utility classes for generating secret keys and QR codes.
- User: Class representing a user.

### constant: Package for storing constants.
- Constants: Class for storing all constants (e.g., folder paths, file formats, string templates, etc.).

### exception: Package for custom exceptions.
- AuthenticationException: Exception for handling authentication errors.

### fabric: Package for creating parsers through a factory.
- ParserFabric: Factory for creating parsers depending on the document type.

### fileparser: Package for parsing financial documents.
- Parser: Interface for parsers.
- BaseParser: Abstract class for basic parsing operations.

### documentParser: Package for specific document parsers.
- CheckParser: Parser for receipts.
- InvoiceParser: Parser for invoices.
- OrderParser: Parser for orders.

### logging: Package for logging information and errors.
- Logger: Class for logging messages to files.

### model: Package with data models representing documents.
- Check: Class representing receipt data.
- Invoice: Class representing invoice data.
- Order: Class representing order data.

### session: Package for managing user sessions.
- SessionManager: Session manager for active sessions.
- Session: Class representing a user session.
- PropertiesLoader: Class for loading configurations from a file.

### service: Utilities for handling files and folders.
- FileService: Class for file operations, including validation, movement, and parsing.

### MainRunner: 
- Main class for managing the program's execution, initiating authentication, file processing, and report generation.
