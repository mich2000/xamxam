/**
 * link to use cloud functions: https://stackoverflow.com/questions/47726099/how-to-write-data-to-firestore-through-firebase-functions
 */

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


exports.onNewUserCreated = functions.auth.user().onCreate((user) => {
    const User = {
        storages: []
    };
    admin.firestore().collection('users').doc(user.uid).set(User);
});

exports.onUserDeleted = functions.auth.user().onDelete((user) => {
    admin.firestore().collection('users').doc(user.uid).delete();
});