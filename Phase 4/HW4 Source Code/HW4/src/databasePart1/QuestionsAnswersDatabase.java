package databasePart1;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import application.Question;
import application.Review;
import application.Answer;
import application.Comment;
import application.Message;

public class QuestionsAnswersDatabase {
	
	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/QuestionAnswerDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null;
		
	public void connectToQuestionAnswerDatabase() throws SQLException {
		System.out.println("tester\n");
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
//			statement.execute("DROP ALL OBJECTS");

			// Create the necessary tables if they don't exist
			createQuestionTable();  
			createAnswerTable();
			createCommentTable();
			createReviewTable();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	public Connection getConnection() {
	    return connection;
	}
	
	public void initializeTestDatabase() throws SQLException {
	    connectToQuestionAnswerDatabase();
	    statement.execute("DROP ALL OBJECTS");
	    createTables();  // Re-create the necessary tables
	}
	
	
	private void createTables() throws SQLException {
		createQuestionTable();
		createAnswerTable();
		createCommentTable();
		createReviewTable();
	}

	// Create the questions table
	private void createQuestionTable() throws SQLException {
		String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "title VARCHAR(255), "
				+ "body TEXT, "
				+ "deleted BOOLEAN DEFAULT FALSE, "
				+ "resolved BOOLEAN DEFAULT FALSE, "
				+ "locked BOOLEAN DEFAULT FALSE)";
		statement.execute(questionTable);
	}
	
	// Create the answers table
	private void createAnswerTable() throws SQLException {
		String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "qID INT,"
				+ "userName VARCHAR(255), "
				+ "content TEXT, "
				+ "resolves BOOLEAN DEFAULT FALSE ,"
				+ "FOREIGN KEY (qID) REFERENCES questions(id) ON DELETE CASCADE)";
		statement.execute(answerTable);
	}
	
	private void createCommentTable() throws SQLException {
		String commentTable = "CREATE TABLE IF NOT EXISTS comments ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "qID INT,"
				+ "userName VARCHAR(255), "
				+ "content TEXT, "
				+ "FOREIGN KEY (qID) REFERENCES questions(id) ON DELETE CASCADE)";
		statement.execute(commentTable);
	}
	
	// Create the reviews table (call this in connectToQuestionAnswerDatabase)
	private void createReviewTable() throws SQLException {
	    String reviewTable = "CREATE TABLE IF NOT EXISTS reviews ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "targetID INT, "                  // ID of the question or answer being reviewed
	            + "targetType VARCHAR(20), "        // e.g., 'question' or 'answer'
	            + "reviewerName VARCHAR(255), "
	            + "reviewText TEXT, "
	            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
	            + ")";
	    statement.execute(reviewTable);
	}

	
	// Count the number of questions
	public int numQuestions() throws SQLException {
		String q = "SELECT COUNT(*) AS count FROM questions";
		ResultSet rs = statement.executeQuery(q);
		if (rs.next()) {
			return rs.getInt("count");
		}
		return 0;
	}
	
	
	public int addQuestion(Question q) throws SQLException {
        String sql = "INSERT INTO questions (userName, title, body, deleted, resolved, locked) VALUES (?, ?, ?, FALSE, FALSE, FALSE)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, q.getUserName());
            ps.setString(2, q.getTitle());
            ps.setString(3, q.getBody());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Unable to add question, no ID obtained.");
    }
	
	
	// Return the title associated with a question id
	public String getTitleFromQuestionID(int id) {
		String q = "SELECT title FROM questions WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("title");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	
	// Return an array containing the user name, title, and body of a question associated with an id
	public String[] getQuestionInfo(int id) {
		String q = "SELECT * FROM questions WHERE id = ?";
		String out[] = new String[3];
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				out[0] = rs.getString("userName");
				out[1] = rs.getString("title");
				out[2] = rs.getString("body");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	
	// Return true if the question associated with an id was created by a specified user
	public boolean doesUserOwnQuestion(int id, String userName) {
		String q = "SELECT userName FROM questions WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String user = rs.getString("userName");
				if (user.equals(userName)) {
					return true;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	// 'Delete' a question by setting the deleted boolean to true
	// Does not actually delete the question so it is still held in the database
	public void deleteQuestion(int id) {
		String q = "UPDATE questions SET deleted = TRUE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return true if a question has been deleted
	public boolean isDeleted(int id) throws SQLException {
		String q = "SELECT deleted FROM questions WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getBoolean("deleted");
	        }
	    }
	    return false;
	}
	
	
	// Set a question to resolved
	public void resolveQuestion(int id) {
		String q = "UPDATE questions SET resolved = TRUE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Set a question to unresolved
	public void unResolveQuestion(int id) {
		String q = "UPDATE questions SET resolved = FALSE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return true if a question has been resolved
	public boolean isResolved(int id){
		String q = "SELECT resolved FROM questions WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("resolved");
	        }
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return false;
	}
	
	
	// Update the body of a question to any new string
	public void updateQuestion(int id, String updatedBody) {	
		String q = "UPDATE questions SET body = ? WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setString(1, updatedBody);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Return the number of answers for any question
	public int numAnswers(int qID) throws SQLException {
		String q = "SELECT COUNT(*) AS count FROM answers WHERE qID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count");
			}
		}
		return 0;
	}
	
	
	// Adds an answer to a question
	// Add answer method with lock check
	public int addAnswer(Answer answer) throws SQLException {
	    // Check if question is locked:
	    String check = "SELECT locked FROM questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(check)) {
	        pstmt.setInt(1, answer.getQID());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next() && rs.getBoolean("locked")) {
	            throw new SQLException("Question is locked. Cannot add answer.");
	        }
	    }
	    // Insert answer
	    String sql = "INSERT INTO answers (qID, userName, content, resolves) VALUES (?, ?, ?, FALSE)";
	    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setInt(1, answer.getQID());
	        ps.setString(2, answer.getUserName());
	        ps.setString(3, answer.getContent());
	        ps.executeUpdate();
	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    }
	    throw new SQLException("Unable to add answer, no ID obtained.");
	}

	
	
	// Return an array list of arrays of strings containing the user name and content associated with answers
	public List<String[]> getAnswers(int qID) {
		String q = "SELECT userName, content FROM answers WHERE qID = ?";
		List<String[]> answers = new ArrayList<>();
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				answers.add(new String[] {rs.getString("userName"), rs.getString("content")});
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return answers;
	}
	
	
	//Return the answer ID associated with a question ID given the number of response
	public int getAnswerID(int qID, int answerCount){
		String q = "SELECT id FROM answers WHERE qID = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, qID);
	        ResultSet rs = pstmt.executeQuery();
	        int counter = 0;
	        while (rs.next()) {
	        	if (answerCount == counter) {
	        		return rs.getInt("id");
	        	}
	        	counter++;
	        }
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return 0;
	}
	
	
	// Sets the answer resolves to true
	public void answerResolves(int id, int answerID) {
		clearResolves(id, answerID);
		String q = "UPDATE answers SET resolves = TRUE WHERE id = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, answerID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean doesAnswerResolve(int answerID) {
		String q = "SELECT resolves FROM answers WHERE id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, answerID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	return rs.getBoolean("resolves");
	        }
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return false;
	}
	
	
	public void clearResolves(int qID, int answerID) {
		String q = "UPDATE answers SET resolves = FALSE WHERE qID = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, qID);
	        pstmt.executeUpdate();
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	}
	
	//
	//
	// Methods used for JUnit Tests
	//
	//
	
	// Edit answer content method
	public void editAnswerContent(int answerID, String newContent) throws SQLException {
	    String q = "UPDATE answers SET content = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setString(1, newContent);
	        pstmt.setInt(2, answerID);
	        pstmt.executeUpdate();
	    }
	}
	
	// Delete answer method
	public void deleteAnswer(int answerID) throws SQLException {
	    String q = "DELETE FROM answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, answerID);
	        pstmt.executeUpdate();
	    }
	}
	
	// Check if answer exists
	public boolean answerExists(int answerID) throws SQLException {
	    String q = "SELECT COUNT(*) AS count FROM answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, answerID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("count") > 0;
	        }
	    }
	    return false;
	}
	
	// Get answer content by answerID
	public String getAnswerContent(int answerID) throws SQLException {
	    String q = "SELECT content FROM answers WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, answerID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("content");
	        }
	    }
	    return null;
	}
	
	// Lock question method
	public void lockQuestion(int qID) throws SQLException {
	    String q = "UPDATE questions SET locked = TRUE WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, qID);
	        pstmt.executeUpdate();
	    }
	}




	
	//comment implementation in database
	
	public int numComments(int qID) throws SQLException {
		String q = "SELECT COUNT(*) AS count FROM comments WHERE qID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("count");
			}
		}
		return 0;
	}
	
	
	// Adds an comment to a question
	public void addComments(Comment comment) throws SQLException{
		String q = "INSERT INTO comments (qID, userName, content) VALUES (?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, comment.getQID());
			pstmt.setString(2, comment.getUserName());
			pstmt.setString(3, comment.getContent());
			pstmt.executeUpdate();
		}
	}
	
	public void deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, commentId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new SQLException("No comment found with the provided ID.");
            }
        }
    }
	
	// Return an array list of arrays of strings containing the user name and content associated with comments
	public List<String[]> getComments(int qID) {
		String q = "SELECT userName, content FROM comments WHERE qID = ?";
		List<String[]> comments = new ArrayList<>();
		
		try (PreparedStatement pstmt = connection.prepareStatement(q)) {
			pstmt.setInt(1, qID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				comments.add(new String[] {rs.getString("userName"), rs.getString("content")});
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return comments;
	}
	
	
	//Return the comment ID associated with a question ID given the number of response
	public int getCommentID(int qID, int commentCount){
		String q = "SELECT id FROM comments WHERE qID = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, qID);
	        ResultSet rs = pstmt.executeQuery();
	        int counter = 0;
	        while (rs.next()) {
	        	if (commentCount == counter) {
	        		return rs.getInt("id");
	        	}
	        	counter++;
	        }
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    }
	    return 0;
	}
	
	//
	//
	// Review Table Functionality
	//
	//

	// Add a new review
	public void addReview(Review review) throws SQLException {
	    String q = "INSERT INTO reviews (targetID, targetType, reviewerName, reviewText) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, review.getTargetID());
	        pstmt.setString(2, review.getTargetType());
	        pstmt.setString(3, review.getReviewerName());
	        pstmt.setString(4, review.getReviewText());
	        pstmt.executeUpdate();
	    }
	}

	// Update an existing review
	public void updateReview(int reviewID, String newReviewText) throws SQLException {
	    String q = "UPDATE reviews SET reviewText = ?, timestamp = CURRENT_TIMESTAMP WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setString(1, newReviewText);
	        pstmt.setInt(2, reviewID);
	        pstmt.executeUpdate();
	    }
	}

	// Delete a review
	public void deleteReview(int reviewID) throws SQLException {
	    String q = "DELETE FROM reviews WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, reviewID);
	        pstmt.executeUpdate();
	    }
	}

	// Retrieve a review for a given answer by a specific reviewer
	public Review getReviewForTargetByReviewer(int targetID, String targetType, String reviewerName) throws SQLException {
	    String q = "SELECT * FROM reviews WHERE targetID = ? AND targetType = ? AND reviewerName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, targetID);
	        pstmt.setString(2, targetType);
	        pstmt.setString(3, reviewerName);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return new Review(
	                rs.getInt("id"),
	                rs.getInt("targetID"),
	                rs.getString("targetType"),
	                rs.getString("reviewerName"),
	                rs.getString("reviewText"),
	                rs.getTimestamp("timestamp")
	            );
	        }
	    }
	    return null;
	}


	// Retrieve all reviews for a given answer (for student view)
	public List<Review> getReviewsForTarget(int targetID, String targetType) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String q = "SELECT * FROM reviews WHERE targetID = ? AND targetType = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(q)) {
	        pstmt.setInt(1, targetID);
	        pstmt.setString(2, targetType);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            reviews.add(new Review(
	                rs.getInt("id"),
	                rs.getInt("targetID"),
	                rs.getString("targetType"),
	                rs.getString("reviewerName"),
	                rs.getString("reviewText"),
	                rs.getTimestamp("timestamp")
	            ));
	        }
	    }
	    return reviews;
	}
	
	//
	//
	//
	//
	// Staff Tests Functionality
	//
	//
	//
	//


    // Checks if a question with the given ID exists in the database.
	public boolean doesQuestionExist(int id) throws SQLException {
	    // Include condition to only count questions that are not deleted
	    String sql = "SELECT COUNT(*) AS count FROM questions WHERE id = ? AND deleted = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("count") > 0;
	            }
	        }
	    }
	    return false;
	}
	
}