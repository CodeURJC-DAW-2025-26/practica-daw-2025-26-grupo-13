import express from 'express';
import mustacheExpress from 'mustache-express';
import bodyParser from 'body-parser';
import { __dirname } from './dirname.js';
import gameRouter from './gameRouter.js';   // Replace gameRouter with the one we have

const app = express();

app.set('views', __dirname + '/../views');      //change the views folder to another one where we have the HTML
app.set('view engine', 'html');
app.engine('html', mustacheExpress(), "html");

app.use(bodyParser.urlencoded({ extended: true }));

app.use(express.static(__dirname + '/../public'));

app.use('/', gameRouter);   // Replace gameRouter with the one we have

app.listen(3000, () => console.log('Listening on port 3000!'));