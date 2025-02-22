# File Storage,Encrypted Sharing and Real-Time Collaboration

File Storage System that enables secure file storage,sharing and real time multi-user collaboration.
### This System has been implemented using:-

| **Component**         | **Technology Used**                          |
|-----------------------|----------------------------------------------|
| **Backend Framework** | Spring Boot (REST API)                       |
| **Authentication**    | JWT + Spring Security                        |
| **Storage**           | MinIO (Object Storage)                       |
| **Database**          | Postgres (Structured Data),MongoDB (For Logs)|
| **Caching**           | Redis + Local Cache                          |
| **Messaging & Sync**  | Redis (Pub / Sub)[Real Time Collaboration]   |
| **Notifications**     | Email & Websocket Alerts                     |
|-----------------------|----------------------------------------------|


## Key Features

### Secure File Storage

- Files are stored securely using MinIO(S3_alternate object storage).
- Files are encrypted before storage to ensure security.

### File Sharing

- Users can share files with other via secure links.
- Token based authentication for secure access.
- Supports temporary access links with expiration times.

### Search & MetaData Indexing

- OpenSearch(**ElasticSearch** alternate)is used for searching of content in files.

### Performance

- Enabled with Local Cache and Redis Cache for quick file access.

### Multi-User Collaboration

- Real-time file editing with Redis Publish/Subscribe for **Instant Synchronization**(*Only this not implemented*).
- Users activity tracking with logged to monitor file modifications.
- Notifications (Email & Websockets) for file upload,download,new version upload,rollback.


### docker-compose

- **Postgres,MongoDB,MinIO,Opensearch** and **Redis** everything under the same network
- ```networks:
         ciphershare-net:
            driver: bridge
- Every Container will have volumes to *persist* the data,so that after restart it doesn't get lost.

## API Endpoints

| **Endpoint**                     | **Method** | **Description**               |
|----------------------------------|------------|-------------------------------|
| **/auth/v1/login**               |    POST    | Authenticate users (jwt)      |
| **/auth/v1/register**            |    POST    | Register new users            |
| **/files/upload**                |    POST    | Upload a file                 |
| **/files/upload-multiple**       |    POST    | Upload multiple file          |
| **/files/delete/{filename}**     |    DELETE  | Delete a file                 |
| **/files/download/{filename}**   |    GET     | Download a file               |
| **/files/search?keyword={query}**|    GET     | Search files using opensearch |
| **/files/metadata/{username}**   |    GET     | Metadata by username          |
| **/files/share/{fileid}**        |    POST    | Sharing file
| **/files/access/{fileid}**       |    POST    | Revoking Access               |
| **/files/version/{fileid}**      |    POST    | Upload new version file       |
| **/log/{username}**              |    GET     | Logs of user                  |
|----------------------------------|------------|-------------------------------|
