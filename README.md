### Be sure to: 
1. Set your password and custom generated secret key in:
> src/main/resources/application.properties
> >app.admin.password=***your_password***<br>
> app.security.secret-key=***your_generated_secret_key***
>
    If admin password remains blank, the password will be defaulted to 'admin'.

    If app.security.secret-key remains blank or the key is less than 32 characters long 
      the application will fail-fast and force a shutdown.
    
    For simplicity reasons a secret key is already provided.

    You can create a new secret key by running: openssl rand -base64 32, or -base64 48
     in a system that has OpenSSL installed. 
    
2. Define your DATABASE information in:

> src/main/resources/application-dev.properties
> >spring.datasource.url=***your_database***<br>
> spring.datasource.username=***your_user***<br>
> spring.datasource.password=***your_user_password***
>
The project is using MySQL by default, so the driver and values will remain as is, for simplicity reasons.

    There is however included a PostgreSQL dependency plugin in build.gradle file.

3. After the first successful run, stop the application and uncomment: 

> src/main/resources/application-dev.properties
> > ***spring.sql.init.mode=always***<br>
***spring.sql.init.data-locations=classpath:sql/all_in_one_to_begin.sql***

then run the application again. This should fill the basic tables with all necessary information, such as Roles, Vehicle Types, Makes and Models etc.
Stop the application and comment back the variables.

    If not, the application will throw an error and crash because the ids will be already in the DB
