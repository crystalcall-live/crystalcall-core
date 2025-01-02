FROM gradle:8-jdk17

WORKDIR /app

COPY .env ./

COPY . .

RUN gradle build -x test

CMD ["gradle", "run"]