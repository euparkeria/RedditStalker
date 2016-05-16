
var redditStalker = angular.module('redditStalker', ['ngRoute','ngAnimate','ngMessages', 'angular-loading-bar', 'highcharts-ng', 'ngTable', 'ngResource']);

// configure our routes
redditStalker.config(function($routeProvider, $locationProvider) {
    $routeProvider

        // redditor search page
        .when('/search/:username?', {
            templateUrl : 'pages/home.html',
            controller  : 'RedditorController'
        })

        // ReddBot's srsusers database
        .when('/SrsUsers', {
            templateUrl : 'pages/srs.html',
            controller  : 'srsController'
        })

        .when('/Subreddits', {
            templateUrl : 'pages/subreddits.html',
            controller  : 'subredditsController'
        })
        .otherwise({redirectTo: "/search"});
    // use the HTML5 History API
    $locationProvider.html5Mode(true);

});



redditStalker.controller('subredditsController', function($scope, NgTableParams, utilityService, $http) {


    $scope.getItems = function() {
        var url = "/subredditsStats";

        var responsePromise = $http.get(url);

        responsePromise.success(function(data, status, headers, config) {

            angular.copy(data, $scope.ajaxData);
            $scope.showResults();


        });
        responsePromise.error(function(data, status, headers, config) {

        });
    };





    $scope.changeSubTableNSFW = function(nsfw){
        console.log(nsfw);
        if(nsfw == false){
            $scope.NewPopTableParams = new NgTableParams({}, { dataset: $scope.tableDataPointer});
        }else{

            $scope.tableData = [];

            $scope.tableDataPointer.forEach(function (sub){
                if(sub.over18 == true){
                    $scope.tableData.push(sub)
                }

            });


            $scope.NewPopTableParams = new NgTableParams({}, { dataset:$scope.tableData});

        }


    };

    $scope.showTopSubTable = function(){
        $scope.tableDataPointer = $scope.ajaxData.top100Subreddits;

        $scope.changeSubTableNSFW($scope.onlyNSFW);





    };

    $scope.showNewPopSubTable = function(){
        $scope.tableDataPointer = $scope.ajaxData.subreddits;

        $scope.changeSubTableNSFW($scope.onlyNSFW);




    };

    $scope.showResults = function(){
        $scope.tableDataPointer = $scope.ajaxData.subreddits;
        $scope.clickedButton = 2;
        $scope.changeSubTableNSFW($scope.onlyNSFW);

        $scope.SubredditTypeGraph.series[0].data[0].y = $scope.ajaxData.publicSubCount;
        $scope.SubredditTypeGraph.series[0].data[1].y = $scope.ajaxData.privateSubCount;
        $scope.SubredditTypeGraph.series[0].data[2].y = $scope.ajaxData.restrictedSubCount;


        $scope.showNewPopTable = true;
        $scope.showGraphs = true;

    };


    $scope.SubredditTypeGraph = {
        options: {
            chart: {
                type: 'pie'
            },

            legend: {
                enabled: false
            },
            plotOptions: {
                pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'

                    }
                }
            }
        },
        title: {
            useHTML:true,
                align: "left",
                text: 'Types of Subreddits',
                x: 50
        },
        series: [{
            name: "Subreddits",
            colorByPoint: true,
            data: [{
                name: "Public",
                y: 0
            }, {
                name: "Private",
                y: 0

            },{
                name: "Restricted",
                y: 0
            }]
        }],

            loading: false
    };



    $scope.ajaxData = {};
    $scope.ajaxData.top100Subreddits = [];
    $scope.ajaxData.subreddits = [];
    $scope.tableDataPointer = null;
    $scope.onlyNSFW = false;

    $scope.showGraphs = false;
    $scope.showNewPopTable = false;

    $scope.clickedButton = 0;


    $scope.fromUnixTime = utilityService.convertTime;

    $scope.getItems();

});


redditStalker.controller('srsController', function($scope, $resource, NgTableParams, utilityService) {

    var Api = $resource("/srs");

    $scope.tableParams = new NgTableParams({}, {
        getData: function(params) {
            // ajax request to api
            return Api.get(params.url()).$promise.then(function(data) {
                params.total(data.totalElements); // recal. page nav controls
                return data.content;
            });
        }
    });

    $scope.fromUnixTime = utilityService.convertTime;


});

//TODO: take into account the username url parameter
redditStalker.controller('navigationMenuController', function($scope, $location) {
    $scope.isActive = function(route) {
        return route === $location.path();
    };

});

redditStalker.filter("rounded",function(){
    return function(val,to){
        return val.toFixed(to || 0);
    }
});


redditStalker.controller('RedditorController', function ($scope, utilityService, redditorService, $route, $routeParams, $http) {

    $scope.getItems = function() {
        var url = "/RedditUser/" + $scope.redditorState.userName;

        var responsePromise = $http.get(url);

        responsePromise.success(function(data, status, headers, config) {
            $scope.redditorState.searchInProgress = false;

            angular.copy(data, $scope.ajaxData);

            //updating the url parameter is only done if it is not set already, otherwise show the results immidiately
            if($routeParams.username != $scope.redditorState.userName){
                $scope.redditorState.firstReload = true;
                $route.updateParams({username:$scope.redditorState.userName});

            }
            else {
                $scope.showSearchResults();
            }


        });
        responsePromise.error(function(data, status, headers, config) {
            $scope.ajaxRequest.serverError = true;

        });
    };

    $scope.searchUser = function(){
        console.log("search user started");
        $scope.resetView();

        $scope.redditorState.searchInProgress = true;

        $scope.getItems();
    };

    $scope.resetView = function() {
        console.log("reset view started");
        $scope.redditorState.showInfoAndButtons = false;
        $scope.redditorState.showContribSortedTable = false;
        $scope.redditorState.showSubmittedLinks = false;
        $scope.redditorState.showSubmittedComments = false;
        $scope.redditorState.showGraphs = false;
        $scope.redditorState.tableIndex = null;
        $scope.redditorState.knownBrigadier = false;
        $scope.redditorState.graphsAlreadyCreated = false;
        $scope.ajaxRequest = {serverError: false};

        console.log("reset view completed" + $scope.redditorState.showInfoAndButtons);

    };

    $scope.knownBrigadierCheck = function(){
      if($scope.ajaxData.knownBrigadier == true){
          $scope.redditorState.knownBrigadier = true;
      }

    };


    $scope.showSearchResults = function(){
        $scope.resetView();
        $scope.redditorState.showInfoAndButtons = true;
        $scope.knownBrigadierCheck();
        $scope.showGraphs();

        $scope.redditorState.lastActive = $scope.getLastActive();
        $scope.redditorState.clickedButton = 3;

    };

    $scope.showSubmittions = function(item, index){
        $scope.redditorState.selectedSubreddit = item.subreddit;
        $scope.redditorState.tableIndex = index;
        if($scope.redditorState.sortedTableSelector == $scope.ajaxData.submittedInSubreddits){
            $scope.redditorState.showSubmittedLinks=true;
        }else if($scope.redditorState.sortedTableSelector == $scope.ajaxData.commentedInSubreddits){
            $scope.redditorState.showSubmittedComments=true;
        }

    };

    $scope.changeTableSource = function(item){
        $scope.redditorState.showContribSortedTable=false;

        $scope.redditorState.showSubmittedLinks=false;
        $scope.redditorState.showSubmittedComments=false;
        $scope.redditorState.showGraphs=false;
        $scope.redditorState.selectedSubreddit = null;
        $scope.redditorState.tableIndex=null;
        $scope.redditorState.sortedTableSelector = item;
        $scope.redditorState.showContribSortedTable=true;

    };


    $scope.createGraphs = function(){

        var hoursCount = [];

        //getting the map's values into the hoursCount array
        Object.keys($scope.ajaxData.postHoursAggr).forEach(function (key) {
            hoursCount.push($scope.ajaxData.postHoursAggr[key])
        });

        //key name must match the one in alignmentSubreddits
        var alignmentScore = {
            "Racism":0,
            "Fascism":0,
            "Libertarian/Ancap":0,
            "Misogyny":0,
            "Religion":0,
            "Liberal/Social-Democracy":0,
            "Feminism":0,
            "Socialism/Marxism":0,
            "Atheism":0

        };



        //combining both submitted in arrays
        var combinedPosts = [];
        combinedPosts.push.apply(combinedPosts,$scope.ajaxData.commentedInSubreddits);
        combinedPosts.push.apply(combinedPosts,$scope.ajaxData.submittedInSubreddits);


        for (var i = 0; i < combinedPosts.length; i++) {

            Object.keys($scope.alignmentSubreddits).forEach(function (key){
                //converts the Political Alignment subreddit lists to lowercase
                $scope.alignmentSubreddits[key] = $scope.alignmentSubreddits[key].join('|').toLowerCase().split('|');

                if($scope.alignmentSubreddits[key].indexOf(combinedPosts[i].subreddit.toLowerCase()) != -1  && combinedPosts[i].KarmaBalance >= combinedPosts[i].count + 5){
                    alignmentScore[key]  += Math.log(combinedPosts[i].count + combinedPosts[i].KarmaBalance);
                }

            });
        }
        console.log(alignmentScore);

        var alighmentScoreValues = [];

        Object.keys(alignmentScore).forEach(function(key){
            alighmentScoreValues.push(alignmentScore[key])

        });

        $scope.redditorGraphs.HourschartConfig.xAxis.categories = Object.keys($scope.ajaxData.postHoursAggr);
        $scope.redditorGraphs.HourschartConfig.series[0].data = hoursCount;

        $scope.redditorGraphs.LinksVsCommentsChartConfig.series[0].data[0].y = $scope.ajaxData.commentsData.length;
        $scope.redditorGraphs.LinksVsCommentsChartConfig.series[0].data[1].y = $scope.ajaxData.submittionsData.length;

        $scope.redditorGraphs.PoliticalChart.xAxis.categories = Object.keys(alignmentScore);
        $scope.redditorGraphs.PoliticalChart.series[0].data = alighmentScoreValues;


    };


    $scope.showGraphs = function(){
        $scope.redditorState.showContribSortedTable=false;
        $scope.redditorState.showSubmittedLinks=false;
        $scope.redditorState.showSubmittedComments=false;
        if($scope.redditorState.graphsAlreadyCreated == false) {
            $scope.createGraphs();
            $scope.redditorState.graphsAlreadyCreated = true;
        }

        $scope.redditorState.showGraphs=true;

    };




    $scope.getCommentLink = function(item){
      return $scope.redditDomain + "/r/" + item.subreddit + "/comments/" + item.linkId.replace("t3_", "") + "/link/" + item.id;

    };




    $scope.getLastActive = function(){

        if($scope.ajaxData.submittionsData[0].createdUtc > $scope.ajaxData.commentsData[0].createdUtc) {
            return $scope.secondsToDays($scope.ajaxData.submittionsData[0].createdUtc)
        } else {
            return $scope.secondsToDays($scope.ajaxData.commentsData[0].createdUtc)
        }


    };


    $scope.ajaxData = redditorService.ajaxData;
    $scope.redditorState = redditorService.redditorState;
    $scope.redditorGraphs = redditorService.redditorGraphs;
    $scope.fromUnixTime = utilityService.convertTime;
    $scope.secondsToDays = utilityService.secondsToDays;

    $scope.alignmentSubreddits = redditorService.alignmentSubreddits;


    $scope.ajaxRequest = {serverError: false};

    $scope.redditDomain = "http://reddit.com";

    //if the username parameter is set and it is not the current RedditorService username than we get the json
    if($routeParams.username != null && $routeParams.username != $scope.redditorState.userName){
        $scope.redditorState.userName = $routeParams.username;
        $scope.searchUser();
    }
    // the controller realods when updating the url user parameter, so we check here and show the results
    if($scope.redditorState.firstReload == true){
        $scope.showSearchResults();
        $scope.redditorState.firstReload = false;
    }



});

redditStalker.service('utilityService', function(){

    this.convertTime =  function (UNIX_timestamp){
        var a = new Date(UNIX_timestamp * 1000);
        var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        var year = a.getFullYear();
        var month = months[a.getMonth()];
        var date = a.getDate();
        var hour = a.getHours();
        var min = a.getMinutes();
        var sec = a.getSeconds();
        return date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec;
    };

    this.secondsToDays = function(UNIX_timestamp){
        var unixNow = Math.floor(Date.now() / 1000);
        return (unixNow - UNIX_timestamp) / 60 / 60 / 24;
    };

});

//Angular service that acts as 'model' to keep all the app data.

redditStalker.service('redditorService', function(){

    this.ajaxData = {
        userData : {},
        submittionsData : [],
        submittedInSubreddits : [],
        commentsData : [],
        commentedInSubreddits : [],
        commentScoreGraphData : [],
        postHoursAggr : {}
    };

    this.redditorState = {
        userName : null,
        lastActive: 0,
        selectedSubreddit : null,
        tableIndex : null,
        showInfoAndButtons : false,
        showContribSortedTable : false,
        showSubmittedLinks : false,
        showSubmittedComments : false,
        sortedTableSelector : null,
        showGraphs:false,
        graphsAlreadyCreated:false,
        clickedButton:null,
        firstReload:false,
        searchInProgress:false,
        knownBrigadier:false
    };

    this.redditorGraphs = {

        PoliticalChart:{

        options: {
            chart: {
                polar: true,
                type: 'area'
            },

            legend: {
                enabled: false
            },

            yAxis: {
                gridLineInterpolation: 'polygon',
                lineWidth: 0,
                min: 0
            },

            tooltip: {
                shared: true,
                pointFormat: '<span style="color:{series.color}">{series.name}: <b>{point.y:,.0f}</b><br/>'
            }
        },
        title: {
            useHTML:true,
            align: "left",
            text: 'Political alignment',
            x: 50

        },

        xAxis: {
            categories: [],
            tickmarkPlacement: 'on',
            lineWidth: 0
        },

        series: [{
            name: 'Alignment',
            data: [],
            color:"#800000",
            pointPlacement: 'on'
        }]
    },



       LinksVsCommentsChartConfig:{
        options: {
            chart: {
                type: 'pie'

            },

            legend: {
                enabled: false
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        format: '<b>{point.name}</b>: {point.percentage:.1f} %'

                    }
                }
            }
        },
        title: {
            useHTML:true,
            align: "left",
            text: 'Submittions and Comments',
            x: 50
        },
        series: [{
            name: "Contributions",
            colorByPoint: true,
            data: [{
                name: "Comments",
                y: 0
            }, {
                name: "Submittions",
                y: 0,
                sliced: true
            }]
        }],

        loading: false
    },




        HourschartConfig : {
        options: {
            chart: {
                type: 'spline'

            },
            yAxis: {
                title: {
                    text: 'Activity'
                }

            },
            legend: {
                enabled: false
            }
        },
        title: {
            useHTML:true,
            align: "left",
            text: 'Most Active Hours of the Day (UTC)',
            x: 50
        },
        series: [{
            name: 'Posts',
            data: []
        }],
        xAxis:{
            categories: [],
            title: {
                text: 'Time of Day'
            }
        },
        loading: false
    }



    };

    this.alignmentSubreddits = {


        "Racism":[

            "WhiteRights",
            "whitebeauty",
            "BlackCrime",
            "odalism",
            "zog",
            "White_Pride",
            "blackpower",
            "100BlackMen",
            "SupportBOBs",
            "trueblackfathers",
            "WhiteNationalism",
            "NorthwestFront",
            "Race_Realism",
            "racism_immigration",
            "HBD",
            "polacks",
            "GreatApes",
            "WhitePolitics",
            "StopWhiteGenocide",
            "White_Music",
            "TrayvonMartin",
            "whiteeurope",
            "Nazi",
            "NationalSocialism",
            "NiggersNews",
            "NiggersStories",
            "Teenapers",
            "FunnyNiggers",
            "ChimpMusic",
            "ShitNiggersSay",
            "NiggerVideos",
            "NiggerDrama",
            "NiggerCartoons",
            "NiggersPics",
            "WatchNiggersDie",
            "TheRacistRedPill",
            "NiggerFacts",
            "NiggersTIL",
            "USBlackCulture",
            "booboons",
            "eugenics"


        ],


        "Fascism":[
            "farright",
            "traditionalist",
            "monarchist",
            "secularright",
            "tea_party",
            "new_right",
            "nationalist",
            "rightist",
            "tea_party",
            "WhiteNationalism",
            "Nazi",
            "Nationalism",
            "European_New_Right",
            "Fascist",
            "third_position",
            "NationalSocialism",
            "Anarcho_Fascism",
            "adolfhitler",
            "eugenics",
            "hbd"


        ],


        "Misogyny":[
            "TheRedPill",
            "RedPillWomen",
            "askTRP",
            "RedPillParenting",
            "thankTRP",
            "becomeaman",
            "altTRP",
            "GEOTRP",
            "TRPOffTopic",
            "MensRights",
            "MensRightsMeta",
            "MensRightsLinks",
            "LadyMRAs",
            "MRRef",
            "MRActivism",
            "FeMRA",
            "Masculism",
            "intactivists",
            "NOMAAM",
            "OneY",
            "MaleGroupTherapy",
            "askmensrights",
            "MaleLifestyle",
            "Seduction",
            "askseddit",
            "fPUA",
            "100sets",
            "ThanksPatriarchy",
            "againstfeminism",
            "askgamergate",
            "pussypass",
            "pussypassdenied",
            "nopussypasshere",
            "RapeHoaxes",
            "maleequality",
            "NewsFlip",
            "amrsucks",
            "booboons",
            "daterape",
            "Ellenpaohate",
            "marriedredpill",
            "AskMRP",
            "hbd",
           "MensRants"

        ],


        "Socialism/Marxism":[
            "Marxism",
            "socialism",
            "Communism",
            "CommunismWorldwide",
            "Anarchism",
            "LeftCommunism",
            "AcademicMarxism",
            "AcademicMarxism",
            "Communism101",
            "Socialism_101",
            "SocialistBooks",
            "CommunistReadings",
            "CommunistMusic",
            "LeftHistoryPorn",
            "SocialistGaming",
            "SocialistMusic",
            "Arbetarrorelsen",
            "AmericanSocialists",
            "BritishCommunists",
            "Canadian_Socialism",
            "Comunismo",
            "NYCSocialism",
            "Rojava",
            "TheIrishLeft",
            "MichiganSocialists",
            "SocialistProgrammers",
            "socialistRA",
            "LateStageCapitalism",
            "CriticalTheory",
            "FULLCOMMUNISM",
            "DebateCommunism",
            "ShitImperialistsSay",
            "youngsocialistunited",
            "ShitLiberalsSay"

        ],


        "Libertarian/Ancap":[
            "Shitstatistssay",
            "Anarcho_Capitalism",
            "Libertarian",
            "invisiblehand",
            "austrian_economics",
            "libertariancomics",
            "libertarianhumor",
            "libertarianmeme",
            "LibertarianWallpapers",
            "libertarian_history",
            "libertarian_music",
            "libertariannews",
            "Voluntarism",
            "Polycentric_Law",
            "MarketAnarchism",
            "GeoLibertarianism",
            "Mutualism",
            "Panarchism",
            "AnCap101",
            "Agorism",
            "libertariandebates",
            "Liberland",
            "Objectivism",
            "LiberalReality",
            "whowillbuildtheroads",
            "LibertarianBestOf",
            "LibertarianMeme",
            "shitpoliticssays",
            "EnoughSandersSpam",
            "SocialismFacts",
            "shitsocialismsays",
            "praxacceptance",
            "AustrianEconomics",
            "Anarcho_Fascism"

        ],


        "Feminism":[

            "Feminism",
            "TwoXchromosomes",
            "feminismformen",
            "AskFeminists",
            "domesticviolence",
            "SexPositive",
            "AnarchaFeminism",
            "Tranarchism",
            "Cyberfeminism",
            "GirlGamers",
            "feministFAQ",
            "GodlessWomen",
            "WhereAretheFeminists",
            "FeministTheory",
            "EcoFeminism",
            "FemmeThoughtsFeminism",
            "Feminisms",
            "FeministHumor",
            "LiberalFeminism",
            "RadicalFeminism",
            "ThirdWaveFeminism",
            "riotgrrrl",
            "womeninphilosophy",
            "FemmeThoughts",
            "MetaFeminism",
            "MensLib",
            "TheBluePill"

        ],

        "Atheism":[
            "Atheism",
            "secular",
            "Atheism_Meta",
            "Atheism2",
            "FreeAtheism",
            "AtheismPlus",
            "AtheistHavens",
            "AtheistParents",
            "atheismindia",
            "GodlessWomen",
            "YoungAtheists",
            "atheismplusmemes",
            "TrueAtheism",
            "AtheistVids",
            "FreeAtheism",
            "RepublicOfAtheism",
            "DebateAnAtheist"



        ],

        "Religion":[
            "Buddhism",
            "Christianity",
            "deism",
            "ProLife",
            "Islam",
            "OrthodoxChristianity",
            "CommunityOfChrist",
            "LatterDaySaints",
            "Pentecostalism",
            "Protestantism",
            "Anglicanism",
            "Catholicism",
            "SpiritFilledBelievers",
            "Bible",
            "Mormon",
            "Judaism",
            "LCMS",
            "openchristian",
            "RadicalChristianity",
            "ChristianAnarchism",
            "ChristianMysticism",
            "ChristianPacifism",
            "ReligiousAnarchism",
            "QUILTBAGChristians",
            "GayChristians",
            "progressive_islam",
            "TrueChristian",
            "ReformedBaptist",
            "ReasonableFaith",
            "FaithandScience",
            "ChristianApologetics",
            "trueprolife",
            "ChristianWomen",
            "cmh",
            "ChristianMusic",
            "UUReddit",
            "Reformed",
            "deism"
        ],


        "Liberal/Social-Democracy":[
            "Liberal",
            "SandersForPresident",
            "greed",
            "elizabethwarren",
            "Progressive",
            "NeoProg",
            "Democracy",
            "DemSocialist",
            "Green",
            "GreenParty",
            "Labor",
            "Occupywallstreet",
            "neoprogs",
            "BasicIncome"

        ]

    }



});





