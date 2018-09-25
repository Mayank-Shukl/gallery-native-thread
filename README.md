# gallery-native-thread

uses java thread ,android Handler based approach to load images in recycler view with caching enabled.
uses data binding for its basic features of recycler view and  it support.
has a glimpse of RX java to make the api hit in one of the 2 API hits.
uses only Gson library to parse response.
min sdk is 21 to avoid : mulitple resource for touch feedback and has not other constraints

Shortcuts:

At some places tight coupling is used which can be replaced with interface interaction
when scroll to end progress bar is not shown but it loads images and data with pagination
notifyDatasetChanged instead of itemRangeInserted is used for a shortcut only
