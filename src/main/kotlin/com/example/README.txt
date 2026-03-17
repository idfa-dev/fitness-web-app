HOW TO ADD DUMMY VALUES TO DATABASE FOR DATABASE TESTING

STEP 1) Based on what you want to insert you want to create a variable which stores the object values...
        So for a user, you will do something like:
            val userexample = User.new {
                type = 1
                username = "competitiveDude"
                password = "comp"
                email = "comp@gmail.com"
                fname = "C-Dawg"
                height = 188.50f
                weight = 93.35f
                dob = "03-03-2004"
                sex = "male"
            }
        As you can see, for float values such as height and weight, you will need "f" at the end. This is because of Kotlin syntax.

STEP 2) This step is optional but you can verify that values have entered correctly by printing something along the lines of:
            println("Created an example user with id = ${userexample.id} and username = ${userexample.username}")
        This will print a message with some values about the created user, but you can replace this with your own values.

STEP 3) To run the sql logger, make sure you click the green triangle go symbol at the top. This will run the entire project and run the sql logger.
        This allows the sql create, insert, delete, etc. messages to be logged and easily seen for testing purposes.