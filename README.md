# CMPT276 Group 17 Project Iteration 3
## Restaurant Inspector

### Members
| ID | Name | Role |
| :--------: | :--- | :---------- |
| smodak | **Sahil Modak** | Product Owner  |
| serikson | **Simon Erikson** | Repo Manager |
| jmt24 | **James Thompson** | Scrum Master |
| yizhongw | **Yizhong Wang** | Team Member |
  
---  

### Features

##### Supported Languages
1. French
2. Germany
3. Itlian
4. Japanese
5. Chinese(Simplified)

##### 1 Search / Filter
Search bar on map
Search bar on list
Search can be focused to different criteria
Different criteria can be combined for a search
  
  
#### 2 Favourites
From list, be able to mark/un-mark restaurants as favourites
Favourite restaurants should be visibly distinct
Upon download of new data, user should be notified of new inspections for favourites
  
  
#### 3  Internationalize
App to support more than one language
App should automatically change to devices set language if that language is supported
All user facing strings should be translated with possible exception of dynamically downloaded strings and errors

---  

### Code review Synopsis  

#### Code review 1
The first code review focused on searching activity  
* Some searches result in crashes 
* Other searches result in no data being shown in different activities  
* Plan to fix these issues required refactoring some adapters to make them easier to use. We also agreed to implement an observer that classes can work with to 'understand' if there is new data to read.  
* We also agreed to changing some UI elements to make them easier to use.  

#### Code review 2
The second code review focused on the favourites.  
* We agreed that the restaurant list adapter should be completely refactored to its own class so it could be reused by multiple activities (specifically the updated favorites user story)  
* We also agreed that some fragements should be made into activities because the activities are easier to use  

#### Code review 3
The last code review focused on code cleanup  
* We agreed that class comments should have consistant javadoc style.  
* We agreed that log statements cluttered up both the code and the log and that we should remove any unnecessary log statemenets to make the code more readible  
* We also agreed that some of the larger classes (Maps) should have some methods grouped by function for easier readibility 


All these issues were addressed for the final build  

---  

#### Project Information

| Resource | Version |
| :--------: | :--- |
|Test Device | Pixel2 API R |
|Minimum SDK | 29 |
|Target SDK | 29 |  
|Compile SDK |  29 |
|Build Tools | 29.0.3 |
|Android Gradle Plugin | 4.0.0 |
|Gradle | 6.1.1 |

---  

#### Acknowledgements
CSV Reading <https://www.callicoder.com/java-read-write-csv-file-opencsv/>  
UpdateRunnable <https://www.youtube.com/watch?v=QfQE1ayCzf8&list=PLrnPJCHvNZuD52mtV8NvazNYIyIVPVZRa>
Save/Load Arraylist to SharedPreferences <https://codinginflow.com/tutorials/android/save-arraylist-to-sharedpreferences-with-gson>

---  
  
#### Citations
###### Icons
<https://icons8.com/icons/set/food-icons/>  
<https://icons8.com/icons/set/equipment/>
<https://icons8.com/icons/set/pest/>  
<https://icons8.com/icons/set/employee/>  
<https://icons8.com/icons/set/license/>  
<https://icons8.com/icons/set/empty/>  
<https://material.io/resources/icons/?style=baseline>

